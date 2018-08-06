package com.xmartlabs.fountain.rx2.common

import java.util.concurrent.Executor


class InstantExecutor : Executor {
  override fun execute(runnable: Runnable) = runnable.run()
}
