package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentDamageRed
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.AccentZenGreen
import com.example.ui.theme.BorderDark26
import com.example.ui.theme.GlassWhite03
import com.example.ui.theme.GlassWhite08
import com.example.ui.theme.SurfaceDark16
import com.example.ui.theme.TextGray8E
import com.example.ui.theme.TextWhiteFA

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
    borderColor: Color = Color.White.copy(alpha = 0.12f),
    cornerRadius: Dp = 24.dp,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun MonkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bg = if (isSecondary) GlassWhite08 else Color.White
    val contentColor = if (isSecondary) TextWhiteFA else Color.Black
    val borderStroke = if (isSecondary) 1.dp else 0.dp
    val borderColor = if (isSecondary) BorderDark26 else Color.Transparent

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) bg else bg.copy(alpha = 0.5f))
            .then(if (borderStroke > 0.dp) Modifier.border(borderStroke, borderColor, RoundedCornerShape(16.dp)) else Modifier)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = if (isSecondary) Color.White else Color.Black),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 15.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MonkProgressIndicator(
    progress: Float, // 0.0 to 1.0+
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.White
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(GlassWhite08)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            indicatorColor.copy(alpha = 0.7f),
                            indicatorColor
                        )
                    )
                )
        )
    }
}

// Map Category strings to specific design colors and adaptive icons
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Fastfood
        "Travel" -> Icons.Default.DirectionsBus
        "Shopping" -> Icons.Default.ShoppingBag
        "Study" -> Icons.Default.School
        "Bills" -> Icons.Default.Receipt
        "Friends" -> Icons.Default.Group
        "Health" -> Icons.Default.LocalHospital
        else -> Icons.Default.QuestionMark // Random & Fallbacks
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> AccentYellow
        "Travel" -> AccentBlue
        "Shopping" -> AccentOrange
        "Study" -> AccentZenGreen
        "Bills" -> AccentDamageRed
        "Friends" -> Color(0xFFC084FC) // soft purple
        "Health" -> Color(0xFF2DD4BF) // calm teal
        else -> TextGray8E // Random
    }
}

// Canvas spending analytics drawings
@Composable
fun WeeklySpendsChart(
    weeklyTotals: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (weeklyTotals.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No spends to visual.", color = TextGray8E, fontSize = 14.sp)
        }
        return
    }

    val maxVal = weeklyTotals.maxOf { it.second }.coerceAtLeast(10.0)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyTotals.forEach { (day, total) ->
            val ratio = (total / maxVal).toFloat().coerceIn(0.01f, 1f)
            var animRatio by remember { mutableStateOf(0f) }

            LaunchedEffect(ratio) {
                animRatio = ratio
            }

            val heightAnim by animateFloatAsState(
                targetValue = animRatio,
                animationSpec = tween(1000)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (total > 0) "$${total.toInt()}" else "",
                    color = if (total > 0) TextWhiteFA else Color.Transparent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // The dynamic vertical graph bar
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(0.75f * heightAnim)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = day,
                    color = if (total > 0) Color.White else TextGray8E,
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun CategorySplitsDonut(
    categoryBreakdown: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val totalSpend = categoryBreakdown.sumOf { it.second }
    if (totalSpend <= 0.0) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Ledger is clear.", color = TextGray8E, fontSize = 14.sp)
        }
        return
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Draw custom Donut Chart in Canvas
        Box(
            modifier = Modifier
                .size(120.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                categoryBreakdown.forEach { (category, value) ->
                    if (value > 0) {
                        val sweep = ((value / totalSpend) * 360f).toFloat()
                        drawArc(
                            color = getCategoryColor(category),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 24.dp.toPx())
                        )
                        startAngle += sweep
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", color = TextGray8E, fontSize = 11.sp)
                Text("$${totalSpend.toInt()}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Legend details
        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categoryBreakdown.sortedByDescending { it.second }.take(4).forEach { (cat, spend) ->
                if (spend > 0) {
                    val pct = (spend / totalSpend * 100).toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(getCategoryColor(cat))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cat, color = TextWhiteFA, fontSize = 12.sp)
                        }
                        Text("$pct% ($${spend.toInt()})", color = TextGray8E, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
