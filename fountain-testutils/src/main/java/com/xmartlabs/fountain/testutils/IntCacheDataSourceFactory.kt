package com.xmartlabs.fountain.testutils

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource

internal class IntCacheDataSourceFactory : DataSource.Factory<Int, Int>() {
  private val items: MutableList<Int> = ArrayList()
  private lateinit var dataSource: MockIntDataSource

  private var created: Boolean = false

  fun clearData() {
    if (created) {
      dataSource.clearData()
    }
  }

  fun addData(items: List<Int>) {
    if (created) {
      dataSource.addData(items)
    } else {
      this.items.addAll(items)
    }
  }

  fun invalidate() {
    if (created) {
      dataSource.invalidate()
    }
  }

  override fun create(): DataSource<Int, Int> {
    created = true
    dataSource = MockIntDataSource(items)
    return dataSource
  }
}

class MockIntDataSource(private val items: MutableList<Int>) : ItemKeyedDataSource<Int, Int>() {
  fun clearData() = items.clear()

  fun addData(items: List<Int>) {
    this.items.addAll(items)

    if (this.items.distinct() != this.items) {
      throw IllegalStateException("There are duplicate elements")
    }
  }

  private fun inRange(position: Int, start: Int, end: Int): Int {
    return when {
      position < start -> start
      position > end -> end
      else -> position
    }
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
    val lastItem = inRange(params.key, 0, items.size)
    val firstItem = inRange(lastItem - params.requestedLoadSize, 0, items.size)
    val data = if (firstItem == lastItem) emptyList<Int>() else items.subList(firstItem, lastItem)
    callback.onResult(data)
  }

  override fun getKey(item: Int): Int = items.indexOfFirst { it == item }
}
