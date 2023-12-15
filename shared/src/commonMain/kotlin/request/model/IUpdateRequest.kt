package com.filamagenta.request.model

interface IUpdateRequest {
    /**
     * Checks whether all the parameters are null.
     *
     * @return `true` if all the properties are null, `false` otherwise.
     */
    fun isEmpty(): Boolean
}
