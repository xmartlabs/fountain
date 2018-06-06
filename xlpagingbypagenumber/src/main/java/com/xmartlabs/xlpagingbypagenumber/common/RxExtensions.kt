package com.xmartlabs.xlpagingbypagenumber.common

import android.support.annotation.AnyThread
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

@AnyThread
internal fun <T> Single<T>.subscribeOn(executor: Executor) = this.subscribeOn(Schedulers.from(executor))

@AnyThread
internal fun <T> Single<T>.observeOn(executor: Executor) = this.observeOn(Schedulers.from(executor))
