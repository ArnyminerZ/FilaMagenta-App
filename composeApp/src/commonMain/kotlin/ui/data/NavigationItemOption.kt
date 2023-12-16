package ui.data

import accounts.Account
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import security.Role

sealed class NavigationItemOption {
    data class DisplayIfWidthSizeClass(
        val acceptableWidthSizeClasses: List<WindowWidthSizeClass>
    ) : NavigationItemOption() {
        /**
         * Checks whether the given [WindowWidthSizeClass] matches one of the stored in [acceptableWidthSizeClasses].
         *
         * @param args 0. The current [WindowWidthSizeClass].
         *
         * @return `true` if [args]`[0]` is one of the classes stored in [acceptableWidthSizeClasses].
         */
        override fun check(vararg args: Any?): Boolean {
            val widthSizeClass = args[0] as WindowWidthSizeClass?
            return acceptableWidthSizeClasses.contains(widthSizeClass)
        }
    }

    data class DisplayIfHasRole(val role: Role) : NavigationItemOption() {
        /**
         * Checks whether the given [Account] has the [role] given.
         *
         * @param args 0. The [Account] to check for.
         *
         * @return `true` if [args]`[0]` has the [role] specified.
         */
        override fun check(vararg args: Any?): Boolean {
            val account = args[0] as Account?
            return if (account == null) {
                false
            } else {
                // todo: Account's roles must be stored
                false
            }
        }
    }

    /**
     * Checks whether the given options match the option.
     *
     * @return `true` if the given [args] match the criteria specified by the option. `false` otherwise.
     */
    abstract fun check(vararg args: Any?): Boolean
}
