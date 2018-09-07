package com.xmartlabs.fountain
/**
 * A structure to handle the network states.
 *
 * @property status The [Status] of the network request.
 * @property throwable The error of the network state.
 * It's present only if the status is [Status.FAILED].
 */
sealed class NetworkState {
  object Success : NetworkState()
  data class Error(val exception: Throwable) : NetworkState()
  data class Loading(val loadingPage : Int) : NetworkState()

  override fun toString(): String {
    return when (this) {
      is Success -> "Success"
      is Error -> "Error[exception=$exception]"
      is Loading -> "Loading[page=$loadingPage]"
    }
  }
}
