package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.MedicationSlot
import com.example.ui.components.MedicationCheckCard
import com.example.ui.components.WaterTrackerCard
import com.example.ui.theme.SeniorCareThemeDefaults

@Composable
fun DailyCareRoute(
    viewModel: DailyCareViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshDateIfNeeded()
    }

    DailyCareScreen(
        uiState = uiState,
        onToggleMedication = viewModel::onToggleMedication,
        onAddWaterCup = viewModel::onAddWaterCup,
        onRemoveWaterCup = viewModel::onRemoveWaterCup,
        onResetToday = viewModel::onResetToday,
        modifier = modifier
    )
}

@Composable
fun DailyCareScreen(
    uiState: DailyCareUiState,
    onToggleMedication: (MedicationSlot) -> Unit,
    onAddWaterCup: () -> Unit,
    onRemoveWaterCup: () -> Unit,
    onResetToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SeniorCareThemeDefaults.colors
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.backgroundGradientTop,
                            colors.backgroundGradientBottom
                        )
                    )
                )
            },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 760.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = if (isWideScreen) 1040.dp else 600.dp)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    DateHeroHeader(
                        yearText = uiState.yearText,
                        monthDayAndWeekday = uiState.monthDayAndWeekday,
                        completedMeds = uiState.medicationsCompletedCount,
                        allMedsDone = uiState.allMedicationsDone,
                        hasAnyRecord = uiState.hasAnyRecord,
                        onResetClick = { showResetDialog = true }
                    )

                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            MedicationSection(
                                uiState = uiState,
                                onToggleMedication = onToggleMedication,
                                modifier = Modifier.weight(1f)
                            )
                            WaterTrackerCard(
                                waterCups = uiState.waterCups,
                                goalCups = uiState.waterGoalCups,
                                onAddCup = onAddWaterCup,
                                onRemoveCup = onRemoveWaterCup,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        MedicationSection(
                            uiState = uiState,
                            onToggleMedication = onToggleMedication
                        )
                        WaterTrackerCard(
                            waterCups = uiState.waterCups,
                            goalCups = uiState.waterGoalCups,
                            onAddCup = onAddWaterCup,
                            onRemoveCup = onRemoveWaterCup
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (showResetDialog) {
        ResetConfirmationDialog(
            onConfirm = {
                showResetDialog = false
                onResetToday()
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
private fun DateHeroHeader(
    yearText: String,
    monthDayAndWeekday: String,
    completedMeds: Int,
    allMedsDone: Boolean,
    hasAnyRecord: Boolean,
    onResetClick: () -> Unit
) {
    val colors = SeniorCareThemeDefaults.colors

    val summaryBg by animateColorAsState(
        targetValue = if (allMedsDone) colors.takenGreenSoft else colors.subtleSurface,
        label = "header_summary_bg"
    )
    val summaryInk by animateColorAsState(
        targetValue = if (allMedsDone) colors.takenGreenOnSoft else colors.inkPrimary,
        label = "header_summary_ink"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_date_header"),
        shape = RoundedCornerShape(28.dp),
        color = colors.cardSurface,
        border = BorderStroke(2.dp, colors.cardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.takenGreenSoft
                    ) {
                        Text(
                            text = stringResource(R.string.today_label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.takenGreenOnSoft,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Text(
                        text = yearText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.inkSecondary
                    )
                }

                val resetDesc = stringResource(R.string.reset_button_desc)
                OutlinedButton(
                    onClick = onResetClick,
                    enabled = hasAnyRecord,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("reset_today_button")
                        .semantics { contentDescription = resetDesc },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (hasAnyRecord) colors.cardBorder else colors.cardBorder.copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = if (hasAnyRecord) colors.inkSecondary else colors.inkMuted.copy(alpha = 0.45f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.reset_button),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (hasAnyRecord) colors.inkSecondary else colors.inkMuted.copy(alpha = 0.45f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Very large today's date display
            Text(
                text = monthDayAndWeekday,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colors.inkPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Reassuring medication status pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = summaryBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (allMedsDone) Icons.Filled.CheckCircle else Icons.Filled.Medication,
                        contentDescription = null,
                        tint = if (allMedsDone) colors.takenGreen else colors.inkSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (allMedsDone) {
                            stringResource(R.string.med_summary_all_done)
                        } else {
                            stringResource(R.string.med_summary_progress, completedMeds)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = summaryInk
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicationSection(
    uiState: DailyCareUiState,
    onToggleMedication: (MedicationSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SeniorCareThemeDefaults.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = stringResource(R.string.section_medication_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = colors.inkPrimary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        MedicationCheckCard(
            slot = MedicationSlot.MORNING,
            isTaken = uiState.morningTaken,
            takenTimeText = uiState.morningTakenTime,
            onToggle = { onToggleMedication(MedicationSlot.MORNING) }
        )

        MedicationCheckCard(
            slot = MedicationSlot.LUNCH,
            isTaken = uiState.lunchTaken,
            takenTimeText = uiState.lunchTakenTime,
            onToggle = { onToggleMedication(MedicationSlot.LUNCH) }
        )

        MedicationCheckCard(
            slot = MedicationSlot.DINNER,
            isTaken = uiState.dinnerTaken,
            takenTimeText = uiState.dinnerTakenTime,
            onToggle = { onToggleMedication(MedicationSlot.DINNER) }
        )
    }
}

@Composable
private fun ResetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = SeniorCareThemeDefaults.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(R.string.reset_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colors.inkPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reset_dialog_message),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.inkSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .defaultMinSize(minHeight = 52.dp)
                    .testTag("reset_confirm_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = colors.onColoredSurface
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_dialog_confirm),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .defaultMinSize(minHeight = 52.dp)
                    .testTag("reset_cancel_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, colors.cardBorder),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_dialog_cancel),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.inkPrimary
                )
            }
        }
    )
}
