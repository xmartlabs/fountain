package com.xmartlabs.fountain.feature.cachednetwork

import android.support.annotation.WorkerThread

interface DataSourceEntityHandler<T> {
  @WorkerThread
  fun saveEntities(response: T?)

  @WorkerThread
  fun dropEntities()

  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}
