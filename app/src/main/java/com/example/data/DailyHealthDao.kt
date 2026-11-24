package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyHealthDao {

    @Query("SELECT * FROM daily_health_records WHERE dateIso = :dateIso LIMIT 1")
    fun observeRecord(dateIso: String): Flow<DailyHealthRecord?>

    @Query("SELECT * FROM daily_health_records WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getRecordOnce(dateIso: String): DailyHealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: DailyHealthRecord)
}
