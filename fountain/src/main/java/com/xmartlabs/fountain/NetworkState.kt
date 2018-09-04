package com.xmartlabs.fountain
/**
 * A structure to handle the network states.
 *
 * @property status The [Status] of the network request.
 * @property throwable The error of the network state.
 * It's present only if the status is [Status.FAILED].
 */
sealed class NetworkState<out R> {
  data class Success<out T>(val data: T) : NetworkState<T>()
  data class Error(val exception: Throwable) : NetworkState<Nothing>()
  data class Loading(val loadingPage : Int) : NetworkState<Nothing>()

  override fun toString(): String {
    return when (this) {
      is Success<*> -> "Success[data=$data]"
      is Error -> "Error[exception=$exception]"
      is Loading -> "Loading[page=$loadingPage]"
    }
  }
}
