package server

import security.Role

abstract class EndpointDef(
    val url: String,
    vararg roles: Role
) {
    /**
     * Holds a list of all the roles required to call this endpoint.
     * Empty if none are required.
     */
    val roles: List<Role> = roles.toList()

    /**
     * Returns [url] with all the parameters defined in [pairs] replaced.
     *
     * Example:
     * ```kotlin
     * // Consider url="/demo/{someId}/path/{otherId}"
     * val result = url("someId" to "replaced", "otherId" to 5)
     * println(result)
     * // /demo/replaced/path/5
     * ```
     * @param pairs All the pairs to be replaced.
     * The first element of the pair matches the name of the element to replace without brackets (`{}`).
     * The second element is what to put instead of the placeholder.
     */
    fun url(vararg pairs: Pair<String, Any>): String {
        var result = url

        for ((key, value) in pairs) {
            result = result.replace("{$key}", value.toString())
        }

        return result
    }
}
