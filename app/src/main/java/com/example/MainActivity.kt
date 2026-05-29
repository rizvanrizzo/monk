package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]
                MonkAppContent(viewModel)
            }
        }
    }
}

@Composable
fun MonkAppContent(viewModel: FinanceViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Determine current onboarding status to route correctly inside Scaffold
    val isOnboarded = userProfile?.isOnboarded == true

    LaunchedEffect(isOnboarded) {
        if (!isOnboarded) {
            viewModel.navigateTo(AppScreen.Onboarding)
        } else if (currentScreen == AppScreen.Onboarding) {
            viewModel.navigateTo(AppScreen.Dashboard)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Black0A // force clean dark black canvas
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen transition manager
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(350))
                },
                label = "temple_transitions"
            ) { screen ->
                when (screen) {
                    AppScreen.Onboarding -> OnboardingScreen(viewModel)
                    AppScreen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        stats = stats,
                        onNavigateToAddExpense = { viewModel.navigateTo(AppScreen.AddExpense) }
                    )
                    AppScreen.AddExpense -> AddExpenseScreen(viewModel)
                    AppScreen.Analytics -> AnalyticsScreen(viewModel)
                    AppScreen.Transactions -> TransactionsScreen(viewModel)
                    AppScreen.Profile -> ProfileScreen(viewModel)
                }
            }

            // Custom Floating Bottom Navigation (visible only if onboarded)
            if (isOnboarded && currentScreen != AppScreen.Onboarding && currentScreen != AppScreen.AddExpense) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .navigationBarsPadding() // gesture pill overlap protection!
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF121212).copy(alpha = 0.9f))
                        .border(1.dp, BorderDark26, RoundedCornerShape(24.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavBarItem(
                            icon = Icons.Default.Home,
                            label = "Home",
                            active = currentScreen == AppScreen.Dashboard,
                            modifier = Modifier.testTag("nav_home")
                        ) {
                            viewModel.navigateTo(AppScreen.Dashboard)
                        }

                        NavBarItem(
                            icon = Icons.Default.TrendingUp,
                            label = "Metrics",
                            active = currentScreen == AppScreen.Analytics,
                            modifier = Modifier.testTag("nav_metrics")
                        ) {
                            viewModel.navigateTo(AppScreen.Analytics)
                        }

                        NavBarItem(
                            icon = Icons.Default.ListAlt,
                            label = "Ledger",
                            active = currentScreen == AppScreen.Transactions,
                            modifier = Modifier.testTag("nav_ledger")
                        ) {
                            viewModel.navigateTo(AppScreen.Transactions)
                        }

                        NavBarItem(
                            icon = Icons.Default.Person,
                            label = "Monk",
                            active = currentScreen == AppScreen.Profile,
                            modifier = Modifier.testTag("nav_profile")
                        ) {
                            viewModel.navigateTo(AppScreen.Profile)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavBarItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (active) 1.15f else 1.0f,
        animationSpec = tween(250)
    )
    val tintColor = if (active) Color.White else TextGray8E

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .scale(scaleAnim)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            color = tintColor,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
    }
}
