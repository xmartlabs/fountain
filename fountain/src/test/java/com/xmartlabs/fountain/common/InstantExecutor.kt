package com.xmartlabs.fountain.common

import java.util.concurrent.Executor


class InstantExecutor : Executor {
  override fun execute(runnable: Runnable) = runnable.run()
}
