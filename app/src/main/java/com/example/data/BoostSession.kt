package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boost_sessions")
data class BoostSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val ramFreedMb: Long,
    val ramUsedPercentBefore: Int,
    val ramUsedPercentAfter: Int,
    val pingBeforeMs: Int,
    val pingAfterMs: Int,
    val batteryTempBefore: Float,
    val batteryLevel: Int,
    val modeUsed: String
)
