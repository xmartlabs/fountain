package com.xmartlabs.sample

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import com.xmartlabs.sample.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

open class App : Application(), HasActivityInjector {
  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      Stetho.initializeWithDefaults(this)
    }
    AppInjector.init(this)
  }

  override fun activityInjector() = dispatchingAndroidInjector
}
