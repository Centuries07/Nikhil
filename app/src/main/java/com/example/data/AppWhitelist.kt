package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "app_whitelist")
data class WhitelistedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isProtected: Boolean = true
)

@Dao
interface WhitelistDao {
    @Query("SELECT * FROM app_whitelist")
    fun getAllWhitelisted(): Flow<List<WhitelistedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setWhitelisted(app: WhitelistedApp)

    @Query("DELETE FROM app_whitelist WHERE packageName = :pkg")
    suspend fun removeWhitelisted(pkg: String)

    @Query("SELECT EXISTS(SELECT 1 FROM app_whitelist WHERE packageName = :pkg AND isProtected = 1)")
    suspend fun isAppWhitelisted(pkg: String): Boolean
}
