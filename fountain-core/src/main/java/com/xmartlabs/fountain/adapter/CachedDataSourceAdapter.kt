package com.xmartlabs.fountain.adapter

import androidx.annotation.WorkerThread
import androidx.paging.DataSource

/** Adapter used to cache the entities in the [DataSource]. */
interface CachedDataSourceAdapter<NetworkValue, DataSourceValue> {
  /** Returns the [DataSource.Factory] that will be used to create the [LivePagedListBuilder]. */
  fun getDataSourceFactory(): DataSource.Factory<*, DataSourceValue>

  /**
   * Saves all entities into the [DataSource].
   * This will be executed in a [transaction][runInTransaction].
   */
  @WorkerThread
  fun saveEntities(response: List<NetworkValue>)

  /**
   * Deletes all cached entities from the [DataSource].
   * This will be executed in a [transaction][runInTransaction].
   */
  @WorkerThread
  fun dropEntities()

  /**
   * Applies multiple [DataSource] operations in a single transaction.
   * The transaction will be marked as successful unless an exception is thrown in the [transaction] function.
   *
   * @param transaction The piece of code to execute.
   */
  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}
