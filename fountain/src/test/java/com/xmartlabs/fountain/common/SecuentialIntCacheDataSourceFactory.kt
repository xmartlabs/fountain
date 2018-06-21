package com.xmartlabs.fountain.common

import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource

class IntCacheDataSourceFactory : DataSource.Factory<Int, Int>() {
  private val items: MutableList<Int> = ArrayList()
  private var dataSource: MockSequentialIntDataSource? = null

  fun clearData() = dataSource!!.clearData()

  fun addData(items: List<Int>) = dataSource!!.addData(items)

  fun invalidate() = dataSource!!.invalidate()

  override fun create(): DataSource<Int, Int> {
    dataSource = MockSequentialIntDataSource(items)
    return dataSource!!
  }
}

class MockSequentialIntDataSource(private val items: MutableList<Int>) : ItemKeyedDataSource<Int, Int>() {
  fun clearData() = items.clear()

  fun addData(items: List<Int>) = this.items.addAll(items)

  private fun inRange(position: Int, start: Int, end: Int): Int {
    if (position < start) return start
    if (position > end) return end
    return position
  }

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int>) {
    val firstItem = inRange(params.requestedInitialKey ?: 0, 0, items.size)
    val lastItem = inRange(firstItem + params.requestedLoadSize, 0, items.size)
    val data = if (firstItem == lastItem) emptyList<Int>() else items.subList(firstItem, lastItem)
    if (params.placeholdersEnabled) {
      callback.onResult(data, firstItem, items.size)
    } else {
      callback.onResult(data)
    }
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int>) {
    val firstItem = inRange(params.key + 1, 0, items.size)
    val lastItem = inRange(firstItem + params.requestedLoadSize, 0, items.size)
    val data = if (firstItem == lastItem) emptyList<Int>() else items.subList(firstItem, lastItem)
    callback.onResult(data)
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int>) {
    val lastItem = inRange(params.key - 1, 0, items.size)
    val firstItem = inRange(lastItem - params.requestedLoadSize, 0, items.size)
    val data = if (firstItem == lastItem) emptyList<Int>() else items.subList(firstItem, lastItem)
    callback.onResult(data)
  }

  override fun getKey(item: Int): Int = item
}
