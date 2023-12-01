package utils

private const val NIF_LENGTH = 9
private const val NIF_LETTER_POS = 8

/**
 * Checks whether `this` [String] is a valid NIF.
 */
val String.isValidNif: Boolean
    get() {
        var nif = this

        // Si el largo del NIF es diferente a 9, acaba el método.
        if (nif.length != NIF_LENGTH) {
            return false
        }

        val secuenciaLetrasNIF = "TRWAGMYFPDXBNJZSQVHLCKE"
        nif = nif.uppercase()

        // Posición inicial: 0 (primero en la cadena de texto).
        // Longitud: cadena de texto menos última posición. Así obtenemos solo el número.
        var numeroNIF: String = nif.substring(0, nif.length - 1)

        // Si es un NIE reemplazamos letra inicial por su valor numérico.
        numeroNIF = numeroNIF.replace("X", "0").replace("Y", "1").replace("Z", "2")

        // Obtenemos la letra con un char que nos servirá también para el índice de las secuenciaLetrasNIF
        val letraNIF: Char = nif[NIF_LETTER_POS]
        val i = numeroNIF.toInt() % secuenciaLetrasNIF.length
        return letraNIF == secuenciaLetrasNIF[i]
    }
