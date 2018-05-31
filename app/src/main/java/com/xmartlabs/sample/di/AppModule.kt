package com.xmartlabs.sample.di

import android.app.Application
import android.arch.persistence.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xmartlabs.template.db.AppDb
import com.xmartlabs.sample.service.UserService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
class AppModule {
  @Singleton
  @Provides
  fun provideGson(): Gson {
    return GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
  }

  @Singleton
  @Provides
  fun provideOkHttpClient(): OkHttpClient{
    val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()
  }

  @Singleton
  @Provides
  fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
  }

  @Singleton
  @Provides
  fun provideGithubService(retrofit: Retrofit) = retrofit.create(UserService::class.java)

  @Singleton
  @Provides
  fun provideDb(app: Application): AppDb {
    return Room
        .databaseBuilder(app, AppDb::class.java, "github.db")
        .fallbackToDestructiveMigration()
        .build()
  }

  @Singleton
  @Provides
  fun provideUserDao(db: AppDb) = db.userDao()
}
