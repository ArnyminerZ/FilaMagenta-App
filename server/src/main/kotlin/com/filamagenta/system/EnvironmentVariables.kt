package com.filamagenta.system

import KoverIgnore

@KoverIgnore
object EnvironmentVariables {
    @KoverIgnore
    object Database {
        @KoverIgnore
        data object Url : EnvironmentVariable<String>("DATABASE_URL", String::class)

        @KoverIgnore
        data object Driver : EnvironmentVariable<String>("DATABASE_DRIVER", String::class)
    }
}
