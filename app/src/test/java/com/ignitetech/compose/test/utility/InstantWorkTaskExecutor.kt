package com.ignitetech.compose.test.utility

import androidx.work.impl.utils.SerialExecutorImpl
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.impl.utils.taskexecutor.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import java.util.concurrent.Executor

class InstantWorkTaskExecutor(
    private val synchronousExecutor: Executor = SynchronousExecutor(),
    private val serialExecutor: SerialExecutor = SerialExecutorImpl(synchronousExecutor)
) : TaskExecutor {

    override fun getMainThreadExecutor(): Executor {
        return synchronousExecutor
    }

    override fun getSerialTaskExecutor(): SerialExecutor {
        return serialExecutor
    }
}
