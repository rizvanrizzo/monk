package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Sealed Screen hierarchy for simple type-safe cinematic state navigation
sealed interface AppScreen {
    object Onboarding : AppScreen
    object Dashboard : AppScreen
    object AddExpense : AppScreen
    object Analytics : AppScreen
    object Transactions : AppScreen
    object Profile : AppScreen
}

data class AnalyticsData(
    val weeklyTotals: List<Pair<String, Double>>, // Day of week to amount
    val categoryBreakdown: List<Pair<String, Double>>, // Category to amount
    val categoryPercentages: List<Pair<String, Float>> // Category to fractional percentage
)

data class DamageReport(
    val grade: String,
    val description: String,
    val hexColor: String,
    val advice: String,
    val percentUsed: Int
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db.transactionDao(), db.userProfileDao())
    }

    // Active screen navigation holder
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Dashboard)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Observe active flows from DB
    val userProfile: StateFlow<UserProfile?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Derived Financial Stats combined from User Profile and Transaction Ledger
    val stats = combine(userProfile, transactions) { profile, txs ->
        val budget = profile?.monthlyBudget ?: 2500.0
        val totalSpent = txs.sumOf { it.amount }
        val remaining = budget - totalSpent
        val savings = if (remaining > 0) remaining else 0.0

        // Auto-streak calculator for "No Spend Day"
        val streak = calculateNoSpendStreak(txs)

        UserProfileStats(
            budget = budget,
            totalSpent = totalSpent,
            balance = remaining,
            savings = savings,
            streak = streak,
            username = profile?.username ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfileStats()
    )

    // Quotes pool
    val financeQuotes = listOf(
        "Small savings become freedom.",
        "Discipline creates peace.",
        "Spend with intention.",
        "Do you need it, or are you just empty?",
        "First control your desires, then control your life.",
        "Monk discipline: Seek wealth in simplicity.",
        "The best things in life aren't on Apple Pay."
    )

    fun getRandomQuote(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return financeQuotes[dayOfYear % financeQuotes.size]
    }

    // Generate dynamic "AI" style financial insights
    val insights = combine(transactions, stats) { txs, currentStats ->
        val list = mutableListOf<String>()

        if (txs.isEmpty()) {
            list.add("Ledger is empty. Save your first expense to generate digital monk insights.")
            list.add("Your streak is just starting. Embrace discipline.")
        } else {
            // High expense day insight
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            val dayGroups = txs.groupBy {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.timestamp
                dayFormat.format(cal.time)
            }
            val highestDay = dayGroups.maxByOrNull { group -> group.value.sumOf { it.amount } }
            if (highestDay != null) {
                list.add("${highestDay.key} is historically your heaviest spending day ($${String.format("%.2f", highestDay.value.sumOf { it.amount })}).")
            }

            // Top category damage sector
            val categoryGroups = txs.groupBy { it.category }
            val leadingCategory = categoryGroups.maxByOrNull { group -> group.value.sumOf { it.amount } }
            if (leadingCategory != null) {
                list.add("You spend the most on ${leadingCategory.key} ($${String.format("%.2f", leadingCategory.value.sumOf { it.amount })}). Stay alert.")
            }

            // Time based insight simulation
            val nightSpends = txs.filter {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.timestamp
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                hour >= 20 || hour < 4
            }
            if (nightSpends.isNotEmpty()) {
                val nightSum = nightSpends.sumOf { it.amount }
                list.add("Saved spend after 8PM aggregates to $${String.format("%.1f", nightSum)}. Watch late night cravings!")
            } else {
                list.add("Early bird discount: No late night transactions logged recently. Excellent monks.")
            }

            // Budget exhaustion threat warning
            val pct = if (currentStats.budget > 0) (currentStats.totalSpent / currentStats.budget) * 100 else 0.0
            if (pct > 80) {
                list.add("CODE RED: Budget exhausted by ${String.format("%.1f", pct)}%. Lock your cards immediately.")
            } else if (pct > 50) {
                list.add("Warning: Over half of your allocated allowance has vanished ($${String.format("%.0f", currentStats.totalSpent)}).")
            } else {
                list.add("Calm waters. You've consumed only ${String.format("%.0f", pct)}% of monthly reserve.")
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(
            "Discipline creates peace.",
            "Analyzing your transactions for monk wisdom..."
        )
    )

    // Dynamic Financial Damage Report based on budget vs spent
    val damageReport = combine(stats, transactions) { s, txs ->
        val budget = s.budget
        val spent = s.totalSpent
        val percent = if (budget > 0) ((spent / budget) * 100).toInt() else 0

        val categoryGroups = txs.groupBy { it.category }
        val topCategory = categoryGroups.maxByOrNull { it.value.sumOf { item -> item.amount } }?.key ?: "None"

        when {
            percent < 10 -> DamageReport(
                grade = "ZEN MASTER",
                description = "Total financial peace. Your pockets are undisturbed.",
                hexColor = "#37C986", // bright neon sage green
                advice = "You possess monk-like restraint. Excellent. Keep it up.",
                percentUsed = percent
            )
            percent in 10..30 -> DamageReport(
                grade = "MINDLESS CHILL",
                description = "Healthy vibe. Minor expenses, everything under control.",
                hexColor = "#44A0FF", // minimal electric blue
                advice = "Spends are within critical margins. Buy that matcha iced tea with pride.",
                percentUsed = percent
            )
            percent in 31..60 -> DamageReport(
                grade = "SLIGHT SCRAPE",
                description = "Getting cozy with your budget. The balance is drifting downwards.",
                hexColor = "#FCD34D", // amber
                advice = "Consider slowing down secondary shopping carts. High density $topCategory detected.",
                percentUsed = percent
            )
            percent in 61..90 -> DamageReport(
                grade = "CRITICAL DAMAGE",
                description = "Pockets are heavily wounded. Budget is gasping for air.",
                hexColor = "#F97316", // orange
                advice = "Avoid convenience items immediately. Highest damage logged in $topCategory.",
                percentUsed = percent
            )
            else -> DamageReport(
                grade = "FINANCIAL APOCALYPSE",
                description = "Total defense failure. Overdrawn on your allowance. Instant ramen awaits.",
                hexColor = "#EF4444", // hot minimalist red
                advice = "EMERGENCY. Delete delivery apps. Meditate on your purchases for 48 hours minimum.",
                percentUsed = percent
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DamageReport("CALCULATING", "Reviewing...", "#FFFFFF", "", 0)
    )

    // Dynamic Analytics Calculations
    val analyticsData = transactions.map { txs ->
        // 1. Weekly Totals
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayTotals = DoubleArray(7)
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getDefault()

        txs.forEach { tx ->
            cal.timeInMillis = tx.timestamp
            // Calendar.DAY_OF_WEEK returns Sunday (1) to Saturday (7)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            // convert to Mon(0) -> Sun(6) for our chart order
            val index = when (dow) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                Calendar.SUNDAY -> 6
                else -> 0
            }
            dayTotals[index] += tx.amount
        }
        val weekly = days.zip(dayTotals.toList())

        // 2. Category totals
        val cats = listOf("Food", "Travel", "Shopping", "Study", "Bills", "Friends", "Health", "Random")
        val catMap = txs.groupBy { it.category }
        val categoryTotals = cats.map { cat ->
            cat to (catMap[cat]?.sumOf { it.amount } ?: 0.0)
        }

        // 3. Category percentages
        val grandTotal = txs.sumOf { it.amount }
        val percentages = categoryTotals.map { (cat, tot) ->
            val pct = if (grandTotal > 0) (tot / grandTotal).toFloat() else 0f
            cat to pct
        }

        AnalyticsData(
            weeklyTotals = weekly,
            categoryBreakdown = categoryTotals,
            categoryPercentages = percentages
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsData(emptyList(), emptyList(), emptyList())
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun completeOnboarding(name: String, income: Double) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = 1,
                username = name.trim(),
                monthlyBudget = income,
                streak = 0,
                isOnboarded = true
            )
            repository.saveUserProfile(profile)
            _currentScreen.value = AppScreen.Dashboard
        }
    }

    fun addExpense(amount: Double, note: String, category: String, date: Long) {
        viewModelScope.launch {
            val tx = Transaction(
                amount = amount,
                note = note.trim(),
                category = category,
                timestamp = date
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            repository.clearAll()
            _currentScreen.value = AppScreen.Onboarding
        }
    }

    // Helper: Dynamic auto-streak of preceding calendar days containing 0 spends
    private fun calculateNoSpendStreak(txs: List<Transaction>): Int {
        if (txs.isEmpty()) return 0

        // Get and align all expense dates in "yyyyMMdd" formats in local timezone
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val spendDates = txs.map { fmt.format(Date(it.timestamp)) }.toSet()

        val todayVal = fmt.format(Date())
        val cal = Calendar.getInstance()

        // If they had an expense today, they can still have a streak in past days.
        // Let's check yesterday as the starting point, or today if they haven't spent anything today!
        val startFromYesterday = spendDates.contains(todayVal)
        if (startFromYesterday) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        var streakCount = 0
        // check up to 365 days backward
        for (i in 0 until 365) {
            val checkDayVal = fmt.format(cal.time)
            if (!spendDates.contains(checkDayVal)) {
                streakCount++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streakCount
    }
}

data class UserProfileStats(
    val budget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val balance: Double = 0.0,
    val savings: Double = 0.0,
    val streak: Int = 0,
    val username: String = ""
)

// Extension functions for kotlinx.coroutines.flow
inline fun <T1, T2, R> combine(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    crossinline transform: suspend (T1, T2) -> R
): kotlinx.coroutines.flow.Flow<R> = kotlinx.coroutines.flow.combine(flow, flow2) { a, b -> transform(a, b) }
