package com.filamagenta.system

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import org.jetbrains.annotations.VisibleForTesting

/**
 * Represents an environment variable with a specific data type.
 *
 * @param DataType the data type for the environment variable value.
 * @property name the name of the environment variable.
 * @property kClass the KClass representing the data type of the environment variable value.
 * @property default If any, the default value to give to this environment variable. Defaults to `null`
 */
sealed class EnvironmentVariable<DataType : Any>(
    private val name: String,
    private val kClass: KClass<DataType>,
    private val default: DataType? = null
) {
    companion object {
        /**
         * Converts a string to the specified data type.
         *
         * @param src The string to be converted.
         * @return The converted value as the specified data type or null if the conversion fails.
         * @throws IllegalArgumentException If the specified data type is not compatible.
         */
        @VisibleForTesting
        @Suppress("UNCHECKED_CAST")
        fun <DataType : Any> convert(name: String, kClass: KClass<DataType>, src: String): DataType? = when (kClass) {
            String::class -> src as? DataType?
            Int::class -> src.toIntOrNull() as? DataType?
            Long::class -> src.toLongOrNull() as? DataType?
            Float::class -> src.toFloatOrNull() as? DataType?
            Double::class -> src.toDoubleOrNull() as? DataType?
            Boolean::class -> src.toBooleanStrictOrNull() as? DataType?
            else -> throw IllegalArgumentException("Type for $name (${kClass.simpleName}) is not compatible.")
        }
    }

    @VisibleForTesting
    @Suppress("VariableNaming", "PropertyName")
    var _value: DataType? = null

    /**
     * Only intended for testing, resets the value of [_value] to the one stored in the environment variable.
     */
    @VisibleForTesting
    fun dispose() {
        _value = System.getenv(name)?.let { convert(name, kClass, it) } ?: default
    }

    /**
     * Retrieves the value of the specified environment variable and converts it to the desired data type.
     *
     * @return The value of the environment variable, converted to the specified data type, or null if the environment
     * variable is not set or could not be converted into [DataType].
     *
     * @throws IllegalArgumentException If the specified data type is not compatible.
     */
    fun get(): DataType? = _value ?: run {
        _value = System.getenv(name)?.let { convert(name, kClass, it) } ?: default
        _value
    }

    /**
     * Returns the value of the DataType.
     *
     * @return The value of the DataType.
     *
     * @throws NullPointerException If the environment variable is not set, or the value could not be converted to the
     * target data type.
     * @throws IllegalArgumentException If the specified data type is not compatible.
     */
    fun getValue(): DataType = get()!!

    operator fun getValue(dataType: DataType?, property: KProperty<*>): DataType {
        return getValue()
    }
}
