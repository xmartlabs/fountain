package com.xmartlabs.fountain.rx2.common

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

internal fun <T> Single<T>.subscribeOn(executor: Executor) = this.subscribeOn(Schedulers.from(executor))

internal fun <T> Single<T>.observeOn(executor: Executor) = this.observeOn(Schedulers.from(executor))
