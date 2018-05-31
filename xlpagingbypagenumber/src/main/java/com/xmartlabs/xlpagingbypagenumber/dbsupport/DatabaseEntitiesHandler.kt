package com.xmartlabs.xlpagingbypagenumber.dbsupport

import android.support.annotation.WorkerThread

interface DatabaseEntitiesHandler<T> {
  @WorkerThread
  fun saveEntities(response: T?)

  @WorkerThread
  fun dropEntities()

  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}