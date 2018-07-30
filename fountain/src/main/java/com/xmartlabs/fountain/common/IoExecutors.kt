package com.xmartlabs.fountain.common

import java.util.concurrent.Executors

class IoExecutors{
  companion object {
    val NETWORK_EXECUTOR by lazy { Executors.newFixedThreadPool(5) }
    val DATABASE_EXECUTOR by lazy { Executors.newSingleThreadExecutor() }
  }
}
