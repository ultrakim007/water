package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.MedicationSlot
import com.example.ui.theme.SeniorCareThemeDefaults

@Composable
fun MedicationCheckCard(
    slot: MedicationSlot,
    isTaken: Boolean,
    takenTimeText: String?,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SeniorCareThemeDefaults.colors
    val haptics = LocalHapticFeedback.current

    val title: String
    val pendingIcon: ImageVector
    val pendingAccent: Color
    val pendingSoftBg: Color
    val tag: String

    when (slot) {
        MedicationSlot.MORNING -> {
            title = stringResource(R.string.med_morning_title)
            pendingIcon = Icons.Rounded.WbTwilight
            pendingAccent = colors.morningAccent
            pendingSoftBg = colors.morningSoft
            tag = "medication_button_morning"
        }
        MedicationSlot.LUNCH -> {
            title = stringResource(R.string.med_lunch_title)
            pendingIcon = Icons.Rounded.WbSunny
            pendingAccent = colors.lunchAccent
            pendingSoftBg = colors.lunchSoft
            tag = "medication_button_lunch"
        }
        MedicationSlot.DINNER -> {
            title = stringResource(R.string.med_dinner_title)
            pendingIcon = Icons.Rounded.NightsStay
            pendingAccent = colors.eveningAccent
            pendingSoftBg = colors.eveningSoft
            tag = "medication_button_dinner"
        }
    }

    val containerColor by animateColorAsState(
        targetValue = if (isTaken) colors.takenGreen else colors.cardSurface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_bg_${slot.name}"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isTaken) colors.takenGreenBorder else colors.cardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_border_${slot.name}"
    )

    val titleColor by animateColorAsState(
        targetValue = if (isTaken) colors.onColoredSurface else colors.inkPrimary,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_title_${slot.name}"
    )

    val subtitleColor by animateColorAsState(
        targetValue = if (isTaken) {
            colors.onColoredSurface.copy(alpha = 0.92f)
        } else {
            colors.inkSecondary
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_subtitle_${slot.name}"
    )

    val statusText = if (isTaken) {
        stringResource(R.string.med_status_taken)
    } else {
        stringResource(R.string.med_status_pending)
    }

    val subtitleText = if (isTaken) {
        if (takenTimeText != null) {
            stringResource(R.string.med_taken_time_format, takenTimeText)
        } else {
            stringResource(R.string.med_status_taken)
        }
    } else {
        stringResource(R.string.med_tap_to_check)
    }

    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggle()
        },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 104.dp)
            .testTag(tag)
            .semantics(mergeDescendants = true) {
                role = Role.Checkbox
                stateDescription = statusText
            },
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(width = 2.dp, color = borderColor),
        shadowElevation = if (isTaken) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Icon Badge
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(if (isTaken) colors.onColoredSurface else pendingSoftBg),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isTaken,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.78f)) togetherWith fadeOut()
                    },
                    label = "med_icon_${slot.name}"
                ) { taken ->
                    if (taken) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.med_icon_taken_desc),
                            tint = colors.takenGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        Icon(
                            imageVector = pendingIcon,
                            contentDescription = stringResource(R.string.med_icon_pending_desc),
                            tint = pendingAccent,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            // Center Title + Helper/Timestamp
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor
                )
            }

            // Right Status Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isTaken) colors.onColoredSurface else colors.subtleSurface,
                border = if (isTaken) null else BorderStroke(1.dp, colors.cardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTaken) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = colors.takenGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isTaken) colors.takenGreen else colors.inkSecondary
                    )
                }
            }
        }
    }
}
