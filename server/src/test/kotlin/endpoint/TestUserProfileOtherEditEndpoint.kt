package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.utils.UserDataKey
import com.filamagenta.endpoint.UserProfileOtherEditEndpoint
import com.filamagenta.request.UserProfileEditRequest
import com.filamagenta.response.Errors
import com.filamagenta.response.FailureResponse
import com.filamagenta.security.Authentication
import com.filamagenta.security.Passwords
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TestUserProfileOtherEditEndpoint : TestServerEnvironment() {
    private fun testUpdate(
        key: UserDataKey,
        value: String,
        assertion: (User) -> Unit
    ) = testServer {
        val user = database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = database { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(user.nif)

        httpClient.post(
            UserProfileOtherEditEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserProfileEditRequest(key, value)
            )
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        database { User[other.id] }.let(assertion)
    }
    private fun testUpdateFails(
        key: UserDataKey?,
        value: String,
        error: Pair<FailureResponse.Error, HttpStatusCode>,
        overrideUserId: Int? = null
    ) = testServer {
        val user = database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = database { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(user.nif)

        httpClient.post(
            UserProfileOtherEditEndpoint.url.replace("{userId}", (overrideUserId ?: other.id.value).toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserProfileEditRequest(key, value)
            )
        }.let { response ->
            assertResponseFailure(response, error)
        }
    }

    @Test
    fun `test updating name`() = testUpdate(
        UserDataKey.Name,
        "New Name"
    ) { assertEquals("New Name", it.name) }

    @Test
    fun `test updating surname`() = testUpdate(
        UserDataKey.Surname,
        "New Surname"
    ) { assertEquals("New Surname", it.surname) }

    @Test
    fun `test updating password`() = testUpdate(
        UserDataKey.Password,
        "This-I5-tH3_n3W.P4\$Sw0RD"
    ) {
        assertTrue(
            Passwords.verifyPassword("This-I5-tH3_n3W.P4\$Sw0RD", it.salt, it.password)
        )
    }

    @Test
    fun `test updating empty name`() = testUpdateFails(
        UserDataKey.Name,
        "",
        Errors.Users.Profile.NameCannotBeEmpty
    )

    @Test
    fun `test updating empty surname`() = testUpdateFails(
        UserDataKey.Surname,
        "",
        Errors.Users.Profile.SurnameCannotBeEmpty
    )

    @Test
    fun `test updating unsafe password`() = testUpdateFails(
        UserDataKey.Password,
        "usafe",
        Errors.Users.Profile.UnsafePassword
    )

    @Test
    fun `test updating unknown user`() = testUpdateFails(
        UserDataKey.Name,
        "New Name",
        Errors.Users.UserIdNotFound,
        10
    )

    @Test
    fun `test updating key failure`() = testUpdateFails(
        null,
        "New Value",
        Errors.Users.Profile.NullKey
    )

    @Test
    fun `test invalid body`() = testServerInvalidBody(
        UserProfileOtherEditEndpoint.url.replace("{userId}", "10"),
        database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
    )
}
