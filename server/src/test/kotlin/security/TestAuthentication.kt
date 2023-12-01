package security

import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.security.Authentication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test

class TestAuthentication {
    @Test
    fun `test creating with nif`() {
        val token = Authentication.generateJWT("123456")

        val jwt = Authentication.verifyJWT(token)
        assertNotNull(jwt)
        assertEquals("123456", jwt.getClaim(AUTH_JWT_CLAIM_NIF).asString())
    }

    @Test
    fun `test creating without nif`() {
        val token = Authentication.generateJWT(null)

        val jwt = Authentication.verifyJWT(token)
        assertNotNull(jwt)
        assertTrue(jwt.getClaim(AUTH_JWT_CLAIM_NIF).isNull)
    }
}
