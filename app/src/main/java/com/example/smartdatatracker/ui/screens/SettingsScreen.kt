package com.example.smartdatatracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartdatatracker.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val settings by settingsViewModel.settings.collectAsState()
    var showSuccess by remember { mutableStateOf(false) }
    var limitInput by remember { mutableStateOf("") }
    var billingDayInput by remember { mutableStateOf("") }

    LaunchedEffect(settings) {
        settings?.let {
            limitInput = it.monthlyLimitGB.toString()
            billingDayInput = it.billingStartDay.toString()
        }
    }

    // Animated background gradient
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Fixed notch problem
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF1A1F35),
                        Color(0xFF0D1120)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with animated icon
        AnimatedContent(
            targetState = showSuccess,
            transitionSpec = {
                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
            },
            label = "header"
        ) { success ->
            if (!success) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Configure your data limits",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF6C63FF).copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF6C63FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            } else {
                // Success message with animation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00C853).copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF00C853),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Settings Saved! 🎉",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your preferences have been updated",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main settings card with glass-morphism
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1F35).copy(alpha = 0.9f),
                            Color(0xFF0D1120).copy(alpha = 0.95f)
                        )
                    )
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Current settings display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SettingsStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Current Limit",
                        value = "${settings?.monthlyLimitGB ?: 0} GB",
                        icon = Icons.Default.DataUsage,
                        color = Color(0xFF6C63FF)
                    )
                    SettingsStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Billing Day",
                        value = "${settings?.billingStartDay ?: 1}",
                        icon = Icons.Default.CalendarToday,
                        color = Color(0xFFFFD93D)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Monthly Limit Input
                Text(
                    text = "📊 Monthly Data Limit",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set your monthly data cap in GB",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedTextField(
                    value = limitInput,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            limitInput = it
                            showSuccess = false
                        }
                    },
                    placeholder = "Enter limit in GB",
                    leadingIcon = Icons.Default.DataUsage,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = limitInput.isNotEmpty() && (limitInput.toIntOrNull() ?: 0) <= 0
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Billing Day Input
                Text(
                    text = "📅 Billing Cycle Start Day",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Day of month when your billing cycle begins",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedTextField(
                    value = billingDayInput,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            billingDayInput = it
                            showSuccess = false
                        }
                    },
                    placeholder = "Enter day (1-31)",
                    leadingIcon = Icons.Default.CalendarToday,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = billingDayInput.isNotEmpty() &&
                            (billingDayInput.toIntOrNull() !in 1..31)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button with gradient
                AnimatedButton(
                    onClick = {
                        val limit = limitInput.toIntOrNull() ?: 30
                        val day = billingDayInput.toIntOrNull() ?: 1

                        if (day in 1..31 && limit > 0) {
                            settingsViewModel.saveMonthlyLimit(limit)
                            settingsViewModel.saveBillingStartDay(day)
                            showSuccess = true
                        }
                    },
                    enabled = true,
                    text = "💾 Save Settings",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showSuccess) {
            LaunchedEffect(Unit) {
                delay(3000)
                showSuccess = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1F35).copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Your data usage will reset automatically on the billing start day each month",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SettingsStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1120).copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardOptions: KeyboardOptions,
    isError: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> Color(0xFFFF6B6B)
            value.isNotEmpty() -> Color(0xFF6C63FF)
            else -> Color.White.copy(alpha = 0.2f)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (value.isNotEmpty()) 8.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.3f),
            focusedLabelColor = Color(0xFF6C63FF),
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF0D1120).copy(alpha = 0.6f),
            unfocusedContainerColor = Color(0xFF0D1120).copy(alpha = 0.3f),
            errorBorderColor = Color(0xFFFF6B6B),
        ),
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (value.isNotEmpty()) Color(0xFF6C63FF) else Color.White.copy(alpha = 0.3f)
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 14.sp
            )
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = if (placeholder.contains("GB")) {
                        "Please enter a valid limit (greater than 0)"
                    } else {
                        "Please enter a valid day (1-31)"
                    },
                    color = Color(0xFFFF6B6B),
                    fontSize = 10.sp
                )
            }
        }
    )
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 16.dp else 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF3D3B8A)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Gray.copy(alpha = 0.1f)
                        )
                    )
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}