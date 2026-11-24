package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MedicationSlot {
    MORNING,
    LUNCH,
    DINNER
}

@Entity(tableName = "daily_health_records")
data class DailyHealthRecord(
    @PrimaryKey
    val dateIso: String,
    val morningTaken: Boolean = false,
    val morningTakenAt: Long? = null,
    val lunchTaken: Boolean = false,
    val lunchTakenAt: Long? = null,
    val dinnerTaken: Boolean = false,
    val dinnerTakenAt: Long? = null,
    val waterCups: Int = 0
)
