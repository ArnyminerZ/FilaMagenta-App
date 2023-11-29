package com.filamagenta.system

object EnvironmentVariables {
    object Database {
        data object Url : EnvironmentVariable<String>("DATABASE_URL", String::class)
        data object Driver : EnvironmentVariable<String>("DATABASE_DRIVER", String::class)
    }
}
