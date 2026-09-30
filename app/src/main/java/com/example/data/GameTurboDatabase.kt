package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BoostSession::class, WhitelistedApp::class, FpsSession::class], version = 3, exportSchema = false)
abstract class GameTurboDatabase : RoomDatabase() {
    abstract fun boostSessionDao(): BoostSessionDao
    abstract fun whitelistDao(): WhitelistDao
    abstract fun fpsSessionDao(): FpsSessionDao

    companion object {
        @Volatile
        private var INSTANCE: GameTurboDatabase? = null

        fun getDatabase(context: Context): GameTurboDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameTurboDatabase::class.java,
                    "game_turbo_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
