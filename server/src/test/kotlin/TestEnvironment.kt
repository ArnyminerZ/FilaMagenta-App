import com.filamagenta.system.EnvironmentVariables
import org.junit.Before

abstract class TestEnvironment {
    @Before
    fun `define JWT`() {
        EnvironmentVariables.Authentication.Jwt.Secret._value = "secret"
        EnvironmentVariables.Authentication.Jwt.Issuer._value = "http://0.0.0.0:8080/"
        EnvironmentVariables.Authentication.Jwt.Audience._value = "http://0.0.0.0:8080/"
        EnvironmentVariables.Authentication.Jwt.Realm._value = "Access to secure endpoints"
    }
}
