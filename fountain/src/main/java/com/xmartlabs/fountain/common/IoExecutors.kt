package com.xmartlabs.fountain.common

import java.util.concurrent.Executors

internal class IoExecutors{
  companion object {
    internal val NETWORK_EXECUTOR by lazy { Executors.newFixedThreadPool(5) }
    internal val DATABASE_EXECUTOR by lazy { Executors.newSingleThreadExecutor() }
  }
}
