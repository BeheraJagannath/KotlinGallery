package com.example.kotlingallerypro.AppOpenAds;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.greedygame.core.app_open_ads.general.AdOrientation;
import com.greedygame.core.app_open_ads.general.AppOpenAdsEventsListener;
import com.greedygame.core.app_open_ads.general.GGAppOpenAds;
import com.greedygame.core.models.general.AdErrors;


import java.util.Date;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private Activity currentActivity;
    private long loadTime = 0;

    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
//    private AppOpenAd appOpenAd = null;
    private GGAppOpenAds appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private static boolean isShowingAd = false;
    private final OpenApplication openApplication;
    private boolean isFirstTime = true;


    /** Constructor */
    public AppOpenManager(OpenApplication myApplication) {
        this.openApplication = myApplication;
        this.openApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {}

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

//            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    /** Request an ad */
    public void fetchAd() {

        if (isAdAvailable()) {
            return;
        }

//        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
//
//            //     Called when an app open ad has loaded.
//
//            @Override
//            public void onAdLoaded(AppOpenAd ad) {
//
//                if (isFirstTime){
//                    ad.show(currentActivity);
//                    isFirstTime = false;
//                    fetchAd();
//                }
//
//                AppOpenManager.this.appOpenAd = ad;
//                AppOpenManager.this.loadTime = (new Date()).getTime();
//            }
//
//            // Called when an app open ad has failed to load.
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError) {
//                // Handle the error.
//            }
//
//        };

        appOpenAd.setListener(new AppOpenAdsEventsListener() {
            @Override
            public void onAdLoaded() {
                Log.d("GGADS","AppOpenAd loaded");
                if (isFirstTime){
                    appOpenAd.show();
                    isFirstTime = false;
                    
                }
            }
            @Override
            public void onAdLoadFailed(AdErrors cause) {
                Log.d("GGADS","AppOpenAd load failed "+cause);
            }

            @Override
            public void onAdShowFailed() {

            }

            @Override
            public void onAdOpened() {
                Log.d("GGADS","AppOpenAd Opened");
            }
            @Override
            public void onAdClosed() {
                Log.d("GGADS","AppOpenAd closed");
            }

        });
        appOpenAd.loadAd( "float-13354");
        appOpenAd.setOrientation(AdOrientation.PORTRAIT);
        appOpenAd.show() ;

//        appOpenAd.setListener(null);

//
//        AdRequest request = getAdRequest();
//        AppOpenAd.load(
//                openApplication, AD_UNIT_ID, request,
//                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        // We will implement this below.

    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /** Creates and returns ad request. */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /** Utility method that checks if ad exists and can be shown. */
    public boolean isAdAvailable() {

        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }
}
