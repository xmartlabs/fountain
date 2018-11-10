package com.xmartlabs.fountain.coroutines.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

internal fun CoroutineDispatcher.toExecutor(coroutineScope: CoroutineScope) = Executor {
  coroutineScope.launch(this@toExecutor) {
    it.run()
  }
}
