package com.xmartlabs.fountain.adapter

import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.support.annotation.AnyThread
import android.support.annotation.WorkerThread

/** It is an adapter that the library will use to cache the entities in the [DataSource]. */
interface CachedDataSourceAdapter<T> {
  /** Returns the [DataSource.Factory] that will be used to create the [LivePagedListBuilder] */
  @AnyThread
  fun getDataSourceFactory(): DataSource.Factory<*, T>

  /**
   * It is used to save all entities into the [DataSource].
   * This will be executed in a [transaction][runInTransaction].
   */
  @WorkerThread
  fun saveEntities(response: List<T>)

  /**
   * It is used to delete all cached entities from the [DataSource].
   * This will be executed in a [transaction][runInTransaction].
   */
  @WorkerThread
  fun dropEntities()

  /**
   * It is used to apply multiple [DataSource] operations in a single transaction.
   * The transaction will be marked as successful unless an exception is thrown in the [transaction] function.
   *
   * @param transaction The piece of code to execute.
   */
  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}
