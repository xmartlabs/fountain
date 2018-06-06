package com.xmartlabs.xlpagingbypagenumber.common

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by mirland on 31/05/18.
 */
internal class IoExecutors{
  companion object {
    internal val NETWORK_EXECUTOR by lazy { Executors.newFixedThreadPool(5) }
    internal val DATABASE_EXECUTOR by lazy { Executors.newSingleThreadExecutor() }
  }
}
