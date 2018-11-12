package com.xmartlabs.sample.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.xmartlabs.sample.App
import dagger.android.AndroidInjection
import dagger.android.support.HasSupportFragmentInjector

/** Helper class to automatically inject */
object AppInjector {
    fun init(app: App) {
        DaggerAppComponent.builder()
            .application(app)
            .build()
            .inject(app)
        app
            .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                  if (activity is HasSupportFragmentInjector) {
                    AndroidInjection.inject(activity)
                  }
                }

                override fun onActivityStarted(activity: Activity) {}

                override fun onActivityResumed(activity: Activity) {}

                override fun onActivityPaused(activity: Activity) {}

                override fun onActivityStopped(activity: Activity) {}

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

                override fun onActivityDestroyed(activity: Activity) {}
            })
    }
}
