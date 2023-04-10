package com.ignitetech.compose.utility.rules.shell

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ShellCommandRule(
    private val shellCommand: ShellCommand,
    instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
) : TestRule {
    private val uiAutomation = instrumentation.uiAutomation
    private val targetPackage = shellEscape(instrumentation.targetContext.packageName)
    private val shellCommandRunner = AdbShellProvider(uiAutomation)

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                shellCommand(shellCommandRunner, targetPackage)
                base.evaluate()
            }
        }
    }
}

// Characters that have no special meaning to the shell.
private const val SAFE_PUNCTUATION = "@%-_+:,./"

/**
 * Quotes a word so that it can be used, without further quoting,as an argument (or part of an argument)
 * in a shell command. Refer to [androidx.test.runner.permission.UiAutomationShellCommand]
 * for details
 */
fun shellEscape(word: String): String {
    return when {
        word.isEmpty() -> "''"
        word.any { !Character.isLetterOrDigit(it) && SAFE_PUNCTUATION.indexOf(it) == -1 } -> {
            "'${word.replace("'", "'\\''")}'"
        }
        else -> word
    }
}
