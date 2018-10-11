package com.xmartlabs.fountain.coroutines.common

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.Executor

internal fun CoroutineDispatcher.toExecutor(coroutineScope: CoroutineScope) = Executor {
  coroutineScope.launch(this@toExecutor) {
    it.run()
  }
}
