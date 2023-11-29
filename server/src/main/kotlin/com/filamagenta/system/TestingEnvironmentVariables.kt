package com.filamagenta.system

import KoverIgnore
import org.jetbrains.annotations.VisibleForTesting

@KoverIgnore
@VisibleForTesting
object TestingEnvironmentVariables {
    @KoverIgnore
    @VisibleForTesting
    data object VarString : EnvironmentVariable<String>("ENV_TEST_VAR_STR", String::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarStringNull : EnvironmentVariable<String>("ENV_TEST_VAR_STR_NULL", String::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarStringDefault : EnvironmentVariable<String>("ENV_TEST_VAR_STR_NULL", String::class, "default")

    @KoverIgnore
    @VisibleForTesting
    data object VarInteger : EnvironmentVariable<Int>("ENV_TEST_VAR_INT", Int::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarIntegerNull : EnvironmentVariable<Int>("ENV_TEST_VAR_INT_NULL", Int::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarLong : EnvironmentVariable<Long>("ENV_TEST_VAR_INT", Long::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarLongNull : EnvironmentVariable<Long>("ENV_TEST_VAR_INT_NULL", Long::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarFloat : EnvironmentVariable<Float>("ENV_TEST_VAR_FLO", Float::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarFloatNull : EnvironmentVariable<Int>("ENV_TEST_VAR_FLO_NULL", Int::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarDouble : EnvironmentVariable<Double>("ENV_TEST_VAR_FLO", Double::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarDoubleNull : EnvironmentVariable<Double>("ENV_TEST_VAR_FLO_NULL", Double::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarBoolean : EnvironmentVariable<Boolean>("ENV_TEST_VAR_BOO", Boolean::class)

    @KoverIgnore
    @VisibleForTesting
    data object VarBooleanNull : EnvironmentVariable<Int>("ENV_TEST_VAR_BOO_NULL", Int::class)
}
