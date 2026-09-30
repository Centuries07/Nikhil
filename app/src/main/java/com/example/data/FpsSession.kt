package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "fps_sessions")
data class FpsSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameTitle: String,
    val targetFps: Int,
    val averageFps: Float,
    val minFps: Int,
    val maxFps: Int,
    val frameDropsCount: Int,
    val durationSeconds: Int,
    val targetDevice: String = "Vivo T4 / iQOO Z10",
    val timestamp: Long = System.currentTimeMillis(),
    val stabilityScorePercent: Int
)

@Dao
interface FpsSessionDao {
    @Query("SELECT * FROM fps_sessions ORDER BY timestamp DESC")
    fun getAllFpsSessions(): Flow<List<FpsSession>>

    @Insert
    suspend fun insertFpsSession(session: FpsSession): Long

    @Query("DELETE FROM fps_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("DELETE FROM fps_sessions")
    suspend fun clearAllFpsSessions()
}
