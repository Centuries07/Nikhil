package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoostSessionDao {
    @Query("SELECT * FROM boost_sessions ORDER BY timestamp DESC LIMIT 50")
    fun getAllSessions(): Flow<List<BoostSession>>

    @Insert
    suspend fun insertSession(session: BoostSession): Long

    @Query("SELECT COUNT(*) FROM boost_sessions")
    suspend fun getTotalBoostCount(): Int

    @Query("SELECT COALESCE(SUM(ramFreedMb), 0) FROM boost_sessions")
    suspend fun getTotalRamFreedMb(): Long

    @Query("DELETE FROM boost_sessions")
    suspend fun clearAll()
}
