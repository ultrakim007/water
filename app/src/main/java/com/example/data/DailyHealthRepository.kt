package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyHealthRepository(
    private val dao: DailyHealthDao
) {
    fun observeRecordForDate(dateIso: String): Flow<DailyHealthRecord> {
        return dao.observeRecord(dateIso).map { existing ->
            existing ?: DailyHealthRecord(dateIso = dateIso)
        }
    }

    suspend fun toggleMedication(dateIso: String, slot: MedicationSlot, nowMillis: Long = System.currentTimeMillis()) {
        val current = dao.getRecordOnce(dateIso) ?: DailyHealthRecord(dateIso = dateIso)
        val updated = when (slot) {
            MedicationSlot.MORNING -> {
                val nextTaken = !current.morningTaken
                current.copy(
                    morningTaken = nextTaken,
                    morningTakenAt = if (nextTaken) nowMillis else null
                )
            }
            MedicationSlot.LUNCH -> {
                val nextTaken = !current.lunchTaken
                current.copy(
                    lunchTaken = nextTaken,
                    lunchTakenAt = if (nextTaken) nowMillis else null
                )
            }
            MedicationSlot.DINNER -> {
                val nextTaken = !current.dinnerTaken
                current.copy(
                    dinnerTaken = nextTaken,
                    dinnerTakenAt = if (nextTaken) nowMillis else null
                )
            }
        }
        dao.upsertRecord(updated)
    }

    suspend fun addWaterCup(dateIso: String) {
        val current = dao.getRecordOnce(dateIso) ?: DailyHealthRecord(dateIso = dateIso)
        dao.upsertRecord(current.copy(waterCups = (current.waterCups + 1).coerceAtMost(99)))
    }

    suspend fun removeWaterCup(dateIso: String) {
        val current = dao.getRecordOnce(dateIso) ?: DailyHealthRecord(dateIso = dateIso)
        if (current.waterCups > 0) {
            dao.upsertRecord(current.copy(waterCups = current.waterCups - 1))
        }
    }

    suspend fun resetDay(dateIso: String) {
        dao.upsertRecord(DailyHealthRecord(dateIso = dateIso))
    }
}
