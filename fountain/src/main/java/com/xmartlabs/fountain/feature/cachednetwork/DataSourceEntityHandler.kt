package com.xmartlabs.fountain.feature.cachednetwork

import android.arch.paging.DataSource
import android.support.annotation.WorkerThread

interface DataSourceEntityHandler<T> {
  fun getDataSourceFactory(): DataSource.Factory<*, T>

  @WorkerThread
  fun saveEntities(response: List<T>)

  @WorkerThread
  fun dropEntities()

  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}
