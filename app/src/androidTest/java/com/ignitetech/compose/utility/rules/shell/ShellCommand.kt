package com.ignitetech.compose.utility.rules.shell

interface ShellCommand {
    operator fun invoke(shellProvider: ShellProvider, targetPackage: String)
}
