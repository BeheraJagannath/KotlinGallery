package com.example.kotlingallerypro.AppOpenAds

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.greedygame.core.AppConfig
import com.greedygame.core.GreedyGameAds
import com.greedygame.core.app_open_ads.general.AdOrientation
import com.greedygame.core.app_open_ads.general.AppOpenAdsEventsListener
import com.greedygame.core.app_open_ads.general.GGAppOpenAds
import com.greedygame.core.models.general.AdErrors

class OpenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val appConfig = AppConfig.Builder(this)
            .withAppId("63837453")
            .build()

        GreedyGameAds.initWith(appConfig, null)
        MobileAds.initialize(
            this
        ) { }
        appOpenManager =
            AppOpenManager(this)

    }
    companion object {
        lateinit var appOpenManager: AppOpenManager
    }
}
