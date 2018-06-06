package com.xmartlabs.xlpagingbypagenumber

enum class Status {
  RUNNING,
  SUCCESS,
  FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val throwable: Throwable? = null) {
  val msg: String?
    get() = throwable?.message

  companion object {
    val LOADED = NetworkState(Status.SUCCESS)
    val LOADING = NetworkState(Status.RUNNING)
    fun error(throwable: Throwable?) = NetworkState(Status.FAILED, throwable)
  }
}
