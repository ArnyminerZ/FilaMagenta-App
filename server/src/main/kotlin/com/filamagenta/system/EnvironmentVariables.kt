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

    object Authentication {
        object Jwt {
            @KoverIgnore
            data object Secret : EnvironmentVariable<String>("AUTH_JWT_SECRET", String::class)

            @KoverIgnore
            data object Issuer : EnvironmentVariable<String>("AUTH_JWT_ISSUER", String::class)

            @KoverIgnore
            data object Audience : EnvironmentVariable<String>("AUTH_JWT_AUDIENCE", String::class)

            @KoverIgnore
            data object Realm : EnvironmentVariable<String>("AUTH_JWT_REALM", String::class)
        }
    }
}
