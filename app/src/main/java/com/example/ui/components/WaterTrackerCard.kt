package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.SeniorCareThemeDefaults

@Composable
fun WaterTrackerCard(
    waterCups: Int,
    goalCups: Int,
    onAddCup: () -> Unit,
    onRemoveCup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SeniorCareThemeDefaults.colors
    val haptics = LocalHapticFeedback.current
    val goalReached = waterCups >= goalCups

    val messageBg by animateColorAsState(
        targetValue = if (goalReached) colors.takenGreenSoft else colors.waterSoft,
        label = "water_banner_bg"
    )
    val messageInk by animateColorAsState(
        targetValue = if (goalReached) colors.takenGreenOnSoft else colors.waterOnSoft,
        label = "water_banner_ink"
    )

    val encouragementText = when {
        waterCups == 0 -> stringResource(R.string.water_encourage_start)
        waterCups < goalCups -> stringResource(R.string.water_encourage_progress, goalCups - waterCups)
        waterCups == goalCups -> stringResource(R.string.water_encourage_goal_reached)
        else -> stringResource(R.string.water_encourage_extra, waterCups - goalCups)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = colors.cardSurface,
        border = BorderStroke(2.dp, colors.cardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            // Section Title Row with safe -1 correction button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(colors.waterSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint = colors.waterPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.section_water_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.inkPrimary
                    )
                }

                val minusDesc = stringResource(R.string.water_minus_desc)
                OutlinedButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRemoveCup()
                    },
                    enabled = waterCups > 0,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 58.dp, minHeight = 48.dp)
                        .testTag("water_minus_button")
                        .semantics { contentDescription = minusDesc },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (waterCups > 0) colors.cardBorder else colors.cardBorder.copy(alpha = 0.45f)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.water_minus_label),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (waterCups > 0) colors.inkSecondary else colors.inkMuted.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Huge Current Water Count Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_count_display"),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    AnimatedContent(
                        targetState = waterCups,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { it / 2 } + fadeIn()) togetherWith
                                    (slideOutVertically { -it / 2 } + fadeOut())
                            } else {
                                (slideInVertically { -it / 2 } + fadeIn()) togetherWith
                                    (slideOutVertically { it / 2 } + fadeOut())
                            }
                        },
                        label = "water_count_number"
                    ) { count ->
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.waterPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.water_cup_unit),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.inkPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = messageBg,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.water_goal_format, goalCups),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = messageInk,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 8 Visual Water Cup Icons (2 rows x 4 cups)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.subtleSurface)
                    .padding(horizontal = 12.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (rowIndex in 0 until 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (colIndex in 0 until 4) {
                            val cupIndex = rowIndex * 4 + colIndex
                            WaterCupIcon(
                                isFilled = cupIndex < waterCups,
                                cupNumber = cupIndex + 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friendly high-contrast status note
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = messageBg
            ) {
                Text(
                    text = encouragementText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = messageInk,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Primary "+1 Cup" Button
            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddCup()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 72.dp)
                    .testTag("water_add_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.waterPrimary,
                    contentColor = colors.onColoredSurface
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalDrink,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.water_add_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
