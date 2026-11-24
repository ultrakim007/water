package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyHealthRepository
import com.example.data.DateFormatterHelper
import com.example.data.KoreanDateHeader
import com.example.data.MedicationSlot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DailyCareUiState(
    val dateIso: String = "",
    val yearText: String = "",
    val monthDayAndWeekday: String = "",
    val morningTaken: Boolean = false,
    val morningTakenTime: String? = null,
    val lunchTaken: Boolean = false,
    val lunchTakenTime: String? = null,
    val dinnerTaken: Boolean = false,
    val dinnerTakenTime: String? = null,
    val waterCups: Int = 0,
    val waterGoalCups: Int = 8
) {
    val medicationsCompletedCount: Int
        get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }

    val allMedicationsDone: Boolean
        get() = morningTaken && lunchTaken && dinnerTaken

    val hasAnyRecord: Boolean
        get() = morningTaken || lunchTaken || dinnerTaken || waterCups > 0
}

@OptIn(ExperimentalCoroutinesApi::class)
class DailyCareViewModel(
    private val repository: DailyHealthRepository
) : ViewModel() {

    private val currentDateHeader = MutableStateFlow(DateFormatterHelper.currentDateHeader())

    val uiState: StateFlow<DailyCareUiState> = currentDateHeader
        .flatMapLatest { header ->
            repository.observeRecordForDate(header.dateIso).map { record ->
                DailyCareUiState(
                    dateIso = header.dateIso,
                    yearText = header.yearText,
                    monthDayAndWeekday = header.monthDayAndWeekday,
                    morningTaken = record.morningTaken,
                    morningTakenTime = DateFormatterHelper.formatKoreanTime(record.morningTakenAt),
                    lunchTaken = record.lunchTaken,
                    lunchTakenTime = DateFormatterHelper.formatKoreanTime(record.lunchTakenAt),
                    dinnerTaken = record.dinnerTaken,
                    dinnerTakenTime = DateFormatterHelper.formatKoreanTime(record.dinnerTakenAt),
                    waterCups = record.waterCups
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = initialHeaderState(currentDateHeader.value)
        )

    fun refreshDateIfNeeded() {
        val latest = DateFormatterHelper.currentDateHeader()
        if (latest.dateIso != currentDateHeader.value.dateIso ||
            latest.monthDayAndWeekday != currentDateHeader.value.monthDayAndWeekday
        ) {
            currentDateHeader.value = latest
        }
    }

    fun onToggleMedication(slot: MedicationSlot) {
        refreshDateIfNeeded()
        val dateIso = currentDateHeader.value.dateIso
        viewModelScope.launch {
            repository.toggleMedication(dateIso = dateIso, slot = slot)
        }
    }

    fun onAddWaterCup() {
        refreshDateIfNeeded()
        val dateIso = currentDateHeader.value.dateIso
        viewModelScope.launch {
            repository.addWaterCup(dateIso = dateIso)
        }
    }

    fun onRemoveWaterCup() {
        refreshDateIfNeeded()
        val dateIso = currentDateHeader.value.dateIso
        viewModelScope.launch {
            repository.removeWaterCup(dateIso = dateIso)
        }
    }

    fun onResetToday() {
        refreshDateIfNeeded()
        val dateIso = currentDateHeader.value.dateIso
        viewModelScope.launch {
            repository.resetDay(dateIso = dateIso)
        }
    }

    private fun initialHeaderState(header: KoreanDateHeader): DailyCareUiState {
        return DailyCareUiState(
            dateIso = header.dateIso,
            yearText = header.yearText,
            monthDayAndWeekday = header.monthDayAndWeekday
        )
    }

    class Factory(
        private val repository: DailyHealthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyCareViewModel::class.java)) {
                return DailyCareViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
