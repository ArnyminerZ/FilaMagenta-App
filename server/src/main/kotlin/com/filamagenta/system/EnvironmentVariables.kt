package com.filamagenta.system

import KoverIgnore

@KoverIgnore
@Suppress("MagicNumber")
object EnvironmentVariables {
    @KoverIgnore
    object Database {
        @KoverIgnore
        data object Url : EnvironmentVariable<String>("DATABASE_URL", String::class)

        @KoverIgnore
        data object Driver : EnvironmentVariable<String>("DATABASE_DRIVER", String::class)
    }

    @KoverIgnore
    object Security {
        @KoverIgnore
        object RateLimit {
            @KoverIgnore
            data object Capacity : EnvironmentVariable<Int>("RATE_CAPACITY", Int::class, 5)

            @KoverIgnore
            data object RefillPeriod : EnvironmentVariable<Int>("RATE_PERIOD", Int::class, 60)
        }
    }

    @KoverIgnore
    object Authentication {
        @KoverIgnore
        object Users {
            @KoverIgnore
            data object AdminNif : EnvironmentVariable<String>("ADMIN_NIF", String::class, "87654321X")

            @KoverIgnore
            data object AdminPwd : EnvironmentVariable<String>("ADMIN_PWD", String::class, "0changeMe!")

            @KoverIgnore
            data object AdminName : EnvironmentVariable<String>("ADMIN_NAME", String::class, "Admin")

            @KoverIgnore
            data object AdminSurname : EnvironmentVariable<String>("ADMIN_SURNAME", String::class, "Admin")
        }

        @KoverIgnore
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
