package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

data class InstalledGame(
    val packageName: String,
    val appName: String,
    val isBgmi: Boolean = false
)

class GameLauncherHelper(private val context: Context) {

    val bgmiPackageName = "com.pubg.imobile"
    val pubgGlobalPackageName = "com.tencent.ig"
    val codPackageName = "com.activision.callofduty.shooter"
    val freeFirePackageName = "com.dts.freefiremax"

    fun isBgmiInstalled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    bgmiPackageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(bgmiPackageName, 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getInstalledGames(): List<InstalledGame> {
        val games = mutableListOf<InstalledGame>()
        val pm = context.packageManager

        // Known top games check
        val priorityGames = listOf(
            bgmiPackageName to "BGMI (Battlegrounds Mobile India)",
            pubgGlobalPackageName to "PUBG Mobile Global",
            codPackageName to "Call of Duty: Mobile",
            freeFirePackageName to "Free Fire MAX"
        )

        for ((pkg, label) in priorityGames) {
            try {
                pm.getPackageInfo(pkg, 0)
                games.add(InstalledGame(pkg, label, pkg == bgmiPackageName))
            } catch (ignored: Exception) {}
        }

        // If BGMI is not installed, we always offer BGMI as primary launcher target with direct install/launch
        if (games.none { it.isBgmi }) {
            games.add(0, InstalledGame(bgmiPackageName, "BGMI (Battlegrounds Mobile India)", isBgmi = true))
        }

        return games
    }

    fun launchBgmiOrMarket(): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(bgmiPackageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            try {
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$bgmiPackageName"))
                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(marketIntent)
                true
            } catch (e: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$bgmiPackageName"))
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
                true
            }
        }
    }

    fun launchPackage(packageName: String): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            false
        }
    }
}
