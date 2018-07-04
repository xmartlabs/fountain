package com.xmartlabs.fountain

/** Represents the possible status of a service call */
enum class Status {
  /** Represents that the service call is running. */
  RUNNING,
  /** Represents that the service call executed successfully. */
  SUCCESS,
  /** Represents that the service call failed. */
  FAILED
}

/**
 * It's a structure to handle the different network states.
 *
 * @property status The [Status] of the network request.
 * @property throwable The error of the network state.
 * It's present only if the status is [Status.FAILED].
 */
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val throwable: Throwable? = null) {

  companion object {
    /** Returns a [NetworkState] with a [Status.SUCCESS] status. */
    val LOADED = NetworkState(Status.SUCCESS)
    /** Returns a [NetworkState] with a [Status.RUNNING] status. */
    val LOADING = NetworkState(Status.RUNNING)

    /**
     * Returns a [NetworkState] with a [Status.FAILED] status.
     *
     * @param throwable The error that caused the failure.
     * @return The [NetworkState] with a [Status.FAILED] status.
     */
    fun error(throwable: Throwable?) = NetworkState(Status.FAILED, throwable)
  }
}
