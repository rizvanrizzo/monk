package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// --- 1. ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(viewModel: FinanceViewModel) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var incomeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black0A)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Monk Badge logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GlassWhite08)
                    .border(1.dp, BorderDark26, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "onboarding_steps"
            ) { currentStep ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        1 -> {
                            Text(
                                text = "WHO ARE YOU?",
                                style = MaterialTheme.typography.displaySmall,
                                color = TextWhiteFA,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Every great monk starts with a name.",
                                color = TextGray8E,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    showError = false
                                },
                                placeholder = { Text("E.g., Tyler", color = TextGray8E) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = BorderDark26,
                                    focusedTextColor = TextWhiteFA,
                                    unfocusedTextColor = TextWhiteFA,
                                    focusedContainerColor = SurfaceDark16,
                                    unfocusedContainerColor = SurfaceDark16
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("username_input"),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    if (name.isNotBlank()) step = 2 else showError = true
                                })
                            )

                            if (showError) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Please enter a name to proceed.", color = AccentDamageRed, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            MonkButton(
                                text = "NEXT",
                                onClick = {
                                    if (name.isNotBlank()) {
                                        step = 2
                                    } else {
                                        showError = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            Text(
                                text = "MONTHLY RESERVE",
                                style = MaterialTheme.typography.displaySmall,
                                color = TextWhiteFA,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Set your standard spending limit. Exceeding this represents damaged finances.",
                                color = TextGray8E,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = incomeInput,
                                onValueChange = {
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.all { char -> char.isDigit() }) {
                                        incomeInput = it
                                        showError = false
                                    }
                                },
                                placeholder = { Text("E.g., 2000", color = TextGray8E) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = BorderDark26,
                                    focusedTextColor = TextWhiteFA,
                                    unfocusedTextColor = TextWhiteFA,
                                    focusedContainerColor = SurfaceDark16,
                                    unfocusedContainerColor = SurfaceDark16
                                ),
                                prefix = { Text("$ ", color = TextWhiteFA, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("budget_input"),
                                keyboardActions = KeyboardActions(onDone = {
                                    val inc = incomeInput.toDoubleOrNull() ?: 0.0
                                    if (inc > 0) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        viewModel.completeOnboarding(name, inc)
                                    } else {
                                        showError = true
                                    }
                                })
                            )

                            if (showError) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Please enter a valid monthly budget.", color = AccentDamageRed, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                MonkButton(
                                    text = "BACK",
                                    onClick = { step = 1 },
                                    isSecondary = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                MonkButton(
                                    text = "ENTER TEMPLE",
                                    onClick = {
                                        val inc = incomeInput.toDoubleOrNull() ?: 0.0
                                        if (inc > 0) {
                                            viewModel.completeOnboarding(name, inc)
                                        } else {
                                            showError = true
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f).testTag("enter_button")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. HOME DASHBOARD SCREEN ---
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    stats: UserProfileStats,
    onNavigateToAddExpense: () -> Unit
) {
    val context = LocalContext.current
    var isQuoteTapped by remember { mutableStateOf(false) }
    var currentQuote by remember { mutableStateOf(viewModel.getRandomQuote()) }

    // Dynamic Greeting based on time
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..11 -> "Good morning, Zen master"
            hour in 12..17 -> "Good afternoon, Monk"
            hour in 18..21 -> "Good evening, Elder"
            else -> "Night watch, Quiet monk"
        }
    }

    // Spending ratio calculation
    val spendPct = if (stats.budget > 0) stats.totalSpent / stats.budget else 0.0
    val budgetColor = if (spendPct < 0.6) AccentZenGreen else if (spendPct < 0.9) AccentOrange else AccentDamageRed

    val allTransactions by viewModel.transactions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Black0A)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Dynamic greeting header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = greeting.uppercase(Locale.getDefault()),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextGray8E,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (stats.username.isNotBlank()) stats.username else "Monk Brother",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextWhiteFA,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Primary Financial Summary Capsule (Glassmorphic)
        item {
            GlassCard(
                cornerRadius = 32.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TOTAL BALANCE", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextGray8E)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedBalance = String.format("%.2f", stats.balance)
                    Text(
                        text = "$$formattedBalance",
                        style = MaterialTheme.typography.displayMedium.copy(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.White.copy(alpha = 0.25f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                blurRadius = 30f
                            )
                        ),
                        color = TextWhiteFA,
                        fontWeight = FontWeight.Bold
                    )

                    // Streak Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassWhite08)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "No Spend Streak",
                            tint = if (stats.streak > 0) AccentOrange else TextGray8E,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stats.streak}D STREAK",
                            color = TextWhiteFA,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("BUDGET / INCOME", fontSize = 10.sp, color = TextGray8E)
                        Text(
                            text = "$${String.format("%.0f", stats.budget)}",
                            color = TextWhiteFA,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column {
                        Text("TOTAL SPEND", fontSize = 10.sp, color = TextGray8E)
                        Text(
                            text = "$${String.format("%.0f", stats.totalSpent)}",
                            color = if (spendPct >= 1) AccentDamageRed else TextWhiteFA,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar representing exhaustion limits
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Damage tracking", fontSize = 11.sp, color = TextGray8E)
                        Text("${(spendPct * 100).toInt()}% USED", fontSize = 11.sp, color = budgetColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    MonkProgressIndicator(progress = spendPct.toFloat(), indicatorColor = budgetColor)
                }
            }
        }

        // Animated Zen Quote board (Micro Interaction)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite03)
                    .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                    .clickable {
                        isQuoteTapped = !isQuoteTapped
                        currentQuote = viewModel.getRandomQuote()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Monk Quotes",
                        tint = AccentZenGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "\"$currentQuote\"",
                        color = TextWhiteFA,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rotate quote",
                        tint = TextGray8E,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Quick Command Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("add_expense_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ADD SPEND", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // Smart Micro Insights (Dynamic rule-based)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DASHBOARD INSIGHTS",
                    color = TextGray8E,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val activeInsights by viewModel.insights.collectAsState()
                activeInsights.take(2).forEach { insight ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark16)
                            .border(1.dp, BorderDark26, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = insight,
                            color = TextWhiteFA,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Recent Spends Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT LEDGER",
                        color = TextGray8E,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "VIEW ALL",
                        color = TextWhiteFA,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.Transactions) }
                    )
                }
            }
        }

        if (allTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions logged. Absolute calm.", color = TextGray8E, fontSize = 13.sp)
                }
            }
        } else {
            val limitList = allTransactions.take(3)
            items(limitList, key = { it.id }) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark16)
                        .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(getCategoryColor(tx.category).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(tx.category),
                                contentDescription = tx.category,
                                tint = getCategoryColor(tx.category),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (tx.note.isNotBlank()) tx.note else tx.category,
                                color = TextWhiteFA,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = tx.category,
                                color = TextGray8E,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = "-$${String.format("%.2f", tx.amount)}",
                        color = AccentDamageRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- 3. ADD EXPENSE SCREEN ---
@Composable
fun AddExpenseScreen(viewModel: FinanceViewModel) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf("Food") }
    var selectedDate by remember { mutableStateOf(Date()) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val categories = listOf("Food", "Travel", "Shopping", "Study", "Bills", "Friends", "Health", "Random")

    // Date picker state trigger
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selCal = Calendar.getInstance()
            selCal.set(year, month, dayOfMonth)
            selectedDate = selCal.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Black0A)
            .padding(horizontal = 24.dp)
            .testTag("add_expense_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Nav Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "LOG DAMAGE",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhiteFA,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Massive Amount Input Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark16)
                    .border(1.dp, BorderDark26, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTER AMOUNT",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextGray8E,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("$ ", color = TextGray8E, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    BasicTextField(
                        value = amountText,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null || it.all { ch -> ch.isDigit() }) {
                                amountText = it
                            }
                        },
                        textStyle = MaterialTheme.typography.displayLarge.copy(
                            color = TextWhiteFA,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .width(220.dp)
                            .testTag("amount_textfield"),
                        cursorBrush = Brush.verticalGradient(listOf(Color.White, Color.White))
                    )
                }
            }
        }

        // Notes and Context Card
        item {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("WHAT DID YOU GRAB?", color = TextGray8E, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("E.g., Boba milk tea", color = TextGray8E) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = BorderDark26,
                    focusedTextColor = TextWhiteFA,
                    unfocusedTextColor = TextWhiteFA,
                    focusedContainerColor = SurfaceDark16,
                    unfocusedContainerColor = SurfaceDark16,
                    focusedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_textfield")
            )
        }

        // Category Matrix Select
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT CATEGORY",
                    color = TextGray8E,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunked = categories.chunked(4)
                    chunked.forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { cat ->
                                val isSelected = categorySelected == cat
                                val tintColor = if (isSelected) Color.Black else TextWhiteFA
                                val bg = if (isSelected) Color.White else SurfaceDark16
                                val bCol = if (isSelected) Color.White else BorderDark26

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bg)
                                        .border(1.dp, bCol, RoundedCornerShape(12.dp))
                                        .clickable { categorySelected = cat }
                                        .padding(vertical = 12.dp)
                                        .testTag("category_$cat"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = getCategoryIcon(cat),
                                            contentDescription = null,
                                            tint = if (isSelected) Color.Black else getCategoryColor(cat),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = cat,
                                            color = tintColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Calendar Date selector
        item {
            val dateFmt = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark16)
                    .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                    .clickable { datePickerDialog.show() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextGray8E)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("TRANSACTION DATE", fontSize = 10.sp, color = TextGray8E, fontFamily = FontFamily.Monospace)
                        Text(dateFmt.format(selectedDate), color = TextWhiteFA, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray8E)
            }
        }

        // Primary Save Trigger Action
        item {
            MonkButton(
                text = "LOG SPEND",
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        viewModel.addExpense(amount, noteText, categorySelected, selectedDate.time)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.navigateTo(AppScreen.Dashboard)
                    }
                },
                enabled = amountText.toDoubleOrNull() != null && (amountText.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_expense_action")
            )
        }
    }
}

// --- 4. ANALYTICS SCREEN ---
@Composable
fun AnalyticsScreen(viewModel: FinanceViewModel) {
    val analytics by viewModel.analyticsData.collectAsState()
    val allInsights by viewModel.insights.collectAsState()
    val stats by viewModel.stats.collectAsState()

    val totalSpend = analytics.categoryBreakdown.sumOf { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Black0A)
            .padding(horizontal = 20.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Simple elegant header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "METRICS & CHILL",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextGray8E,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Financial Graph",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextWhiteFA,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Dotted matrix with weekly spending bars
        item {
            GlassCard {
                Text(
                    text = "WEEKLY SPENDING PATTERNS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextGray8E,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                WeeklySpendsChart(
                    weeklyTotals = analytics.weeklyTotals,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        // Circular progress category breakdown card
        item {
            GlassCard {
                Text(
                    text = "CATEGORY SHARE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextGray8E,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CategorySplitsDonut(
                    categoryBreakdown = analytics.categoryBreakdown,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Streaks display
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark16)
                    .border(1.dp, BorderDark26, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${stats.streak} DAYS WITHOUT SPENDS",
                        color = TextWhiteFA,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (stats.streak > 0) "Your monk self-regulation is exceptional." else "Every zero dollar day increments the streak. Start today.",
                        color = TextGray8E,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Advanced AI style Insights panel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "INTELLIGENT INSIGHTS (MONK AI)",
                    color = TextGray8E,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                allInsights.forEach { insight ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark16)
                            .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Top)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = insight,
                            color = TextWhiteFA,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// --- 5. TRANSACTIONS SCREEN (LEDGER) ---
@Composable
fun TransactionsScreen(viewModel: FinanceViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val allTransactions by viewModel.transactions.collectAsState()
    val categories = listOf("All", "Food", "Travel", "Shopping", "Study", "Bills", "Friends", "Health", "Random")

    // Filter logic
    val filteredList = allTransactions.filter { tx ->
        val matchesQuery = tx.note.contains(searchQuery, ignoreCase = true) || tx.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || tx.category == selectedCategoryFilter
        matchesQuery && matchesCategory
    }

    val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black0A)
            .padding(horizontal = 20.dp)
            .testTag("transactions_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "LEDGER HISTORICS",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = TextGray8E,
                letterSpacing = 1.sp
            )
            Text(
                text = "Total Damage",
                style = MaterialTheme.typography.displayMedium,
                color = TextWhiteFA,
                fontWeight = FontWeight.Bold
            )
        }

        // Minimal Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search descriptor...", color = TextGray8E, fontSize = 14.sp) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray8E) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = BorderDark26,
                focusedTextColor = TextWhiteFA,
                unfocusedTextColor = TextWhiteFA,
                focusedContainerColor = SurfaceDark16,
                unfocusedContainerColor = SurfaceDark16
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_ledger_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal tag filters
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategoryFilter == cat
                val bg = if (isSelected) Color.White else SurfaceDark16
                val textCol = if (isSelected) Color.Black else TextWhiteFA
                val borderCol = if (isSelected) Color.White else BorderDark26

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { selectedCategoryFilter = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("filter_chip_$cat")
                ) {
                    Text(
                        text = cat,
                        color = textCol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Ledger List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Zen Infinite",
                        tint = TextGray8E,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The ledger is empty. Vibe absolute peace.",
                        color = TextGray8E,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag("ledger_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredList, key = { it.id }) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark16)
                            .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(getCategoryColor(tx.category).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(tx.category),
                                    contentDescription = null,
                                    tint = getCategoryColor(tx.category),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (tx.note.isNotBlank()) tx.note else tx.category,
                                    color = TextWhiteFA,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tx.category,
                                        color = getCategoryColor(tx.category),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateFmt.format(Date(tx.timestamp)),
                                        color = TextGray8E,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "-$${String.format("%.2f", tx.amount)}",
                                color = AccentDamageRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Simple responsive tap-to-delete drawer
                            IconButton(
                                onClick = { viewModel.deleteExpense(tx.id) },
                                modifier = Modifier.size(32.dp).testTag("delete_tx_${tx.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = TextGray8E.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 6. PROFILE & SETTINGS SCREEN ---
@Composable
fun ProfileScreen(viewModel: FinanceViewModel) {
    val profileState by viewModel.userProfile.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val damageReport by viewModel.damageReport.collectAsState()

    var editingName by remember { mutableStateOf(profileState?.username ?: "") }
    var editingBudget by remember { mutableStateOf(profileState?.monthlyBudget?.toString() ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    var showDamageOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(profileState) {
        profileState?.let {
            editingName = it.username
            editingBudget = it.monthlyBudget.toString()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Black0A)
                .padding(horizontal = 20.dp)
                .testTag("profile_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            // Profile dynamic code header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "MONK DISCIPLINE CENTER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TextGray8E,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Configuration",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextWhiteFA,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // User Profile Configuration Card
            item {
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROFILE SETTING",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextGray8E
                        )
                        Text(
                            text = if (isEditing) "SAVE" else "EDIT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    if (isEditing) {
                                        val budgetVal = editingBudget.toDoubleOrNull() ?: stats.budget
                                        if (editingName.isNotBlank() && budgetVal > 0) {
                                            viewModel.completeOnboarding(editingName, budgetVal)
                                            isEditing = false
                                        }
                                    } else {
                                        isEditing = true
                                    }
                                }
                                .testTag("profile_edit_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editingName,
                            onValueChange = { editingName = it },
                            label = { Text("MONK NAME", color = TextGray8E, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = BorderDark26,
                                focusedTextColor = TextWhiteFA,
                                unfocusedTextColor = TextWhiteFA
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = editingBudget,
                            onValueChange = { editingBudget = it },
                            label = { Text("MONTHLY LIMIT", color = TextGray8E, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = BorderDark26,
                                focusedTextColor = TextWhiteFA,
                                unfocusedTextColor = TextWhiteFA
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Column {
                            Text("MONK USERNAME", fontSize = 10.sp, color = TextGray8E)
                            Text(
                                stats.username.ifBlank { "Unregistered Br." },
                                color = TextWhiteFA,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("MONTHLY RECONCILE BUDGET", fontSize = 10.sp, color = TextGray8E)
                            Text(
                                "$${String.format("%.2f", stats.budget)}",
                                color = TextWhiteFA,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Financial Damage Report trigger
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FINANCIAL DAMAGE SUMMARY",
                        color = TextGray8E,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark16)
                            .border(1.dp, BorderDark26, RoundedCornerShape(24.dp))
                            .clickable { showDamageOverlay = true }
                            .padding(20.dp)
                            .testTag("trigger_damage_report")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(damageReport.hexColor)))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = damageReport.grade,
                                        color = Color(android.graphics.Color.parseColor(damageReport.hexColor)),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Icon(Icons.Default.Launch, contentDescription = "Open matrix", tint = TextGray8E, modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(damageReport.description, color = TextWhiteFA, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Calculated using active month damage ratios.", color = TextGray8E, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Developer options Reset
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TEMPLE ADMINISTRATION",
                        color = TextGray8E,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark16)
                            .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                            .clickable { viewModel.resetAll() }
                            .padding(16.dp)
                            .testTag("reset_profile_action"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Emergency, contentDescription = "Emergency reset", tint = AccentDamageRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("RESET MONK ACCOUNT STATE", color = AccentDamageRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- FULL SCREEN GLASS DAMAGE MODAL OVERLAY ---
        if (showDamageOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable { showDamageOverlay = false }
                    .padding(24.dp)
                    .testTag("damage_overlay_modal"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceDark16)
                        .border(1.dp, BorderDark26, RoundedCornerShape(28.dp))
                        .padding(24.dp)
                        .clickable(enabled = false) {}, // consume clicks
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(damageReport.hexColor)).copy(alpha = 0.15f))
                            .border(1.dp, Color(android.graphics.Color.parseColor(damageReport.hexColor)).copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OfflineBolt,
                            contentDescription = null,
                            tint = Color(android.graphics.Color.parseColor(damageReport.hexColor)),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "DAMAGE REPORT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextGray8E,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = damageReport.grade,
                        color = Color(android.graphics.Color.parseColor(damageReport.hexColor)),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = damageReport.description,
                        color = TextWhiteFA,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Advice Board
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassWhite03)
                            .border(1.dp, BorderDark26, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("MONK REMEDY", fontSize = 10.sp, color = TextGray8E, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = damageReport.advice,
                                color = TextWhiteFA,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    MonkButton(
                        text = "ACKNOWLEDGE WITH DISCIPLINE",
                        onClick = { showDamageOverlay = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
