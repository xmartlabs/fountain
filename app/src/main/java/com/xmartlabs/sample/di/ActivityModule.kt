package com.xmartlabs.sample.di

import com.xmartlabs.sample.ui.searchusers.ListGithubUsersActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {
  @ContributesAndroidInjector
  abstract fun contributeListUsersActivity(): ListGithubUsersActivity
}
