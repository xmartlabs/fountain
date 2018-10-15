package com.xmartlabs.fountain.testutils

import android.arch.paging.DataSource
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter

class IntMockedCachedDataSourceAdapter(var numberOfErrors: Int = 0) : CachedDataSourceAdapter<Int, Int> {
  private val sequentialIntCacheDataSourceFactory = IntCacheDataSourceFactory()

  override fun getDataSourceFactory(): DataSource.Factory<Int, Int> = sequentialIntCacheDataSourceFactory

  override fun saveEntities(response: List<Int>) {
    sequentialIntCacheDataSourceFactory.addData(response)
  }

  override fun dropEntities() {
    if (this.numberOfErrors > 0) {
      this.numberOfErrors--
      throw IllegalStateException("${this.numberOfErrors} errors remaining.")
    } else {
      sequentialIntCacheDataSourceFactory.clearData()
    }
  }

  override fun runInTransaction(transaction: () -> Unit) {
    transaction.invoke()
    sequentialIntCacheDataSourceFactory.invalidate()
  }
}
