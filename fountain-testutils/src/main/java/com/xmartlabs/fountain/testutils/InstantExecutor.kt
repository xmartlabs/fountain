package com.xmartlabs.fountain.testutils

import java.util.concurrent.Executor


class InstantExecutor : Executor {
  override fun execute(runnable: Runnable) = runnable.run()
}
