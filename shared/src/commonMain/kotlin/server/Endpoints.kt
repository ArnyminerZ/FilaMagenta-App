package server

import KoverIgnore
import security.Roles

@KoverIgnore
object Endpoints {
    object Authenticate {
        data object Login : EndpointDef("/auth/login")

        data object Register : EndpointDef("/auth/register", Roles.Users.Create)
    }

    @KoverIgnore
    object Event {
        data object Create : EndpointDef("/events/create", Roles.Events.Create)

        data object Delete : EndpointDef("/events/{eventId}", Roles.Events.Delete)

        data object Join : EndpointDef("/events/{eventId}/join")

        data object JoinOther : EndpointDef("/events/{eventId}/join/{otherId}", Roles.Events.JoinOthers)

        data object Leave : EndpointDef("/events/{eventId}/leave")

        data object LeaveOther : EndpointDef("/events/{eventId}/leave/{otherId}", Roles.Events.LeaveOthers)

        data object List : EndpointDef("/events/list")

        data object Payment : EndpointDef("/events/{eventId}/payment/{otherId}", Roles.Events.Payment)

        data object Update : EndpointDef("/events/{eventId}", Roles.Events.Update)
    }

    object Security {
        data object RolesList : EndpointDef("/user/roles", Roles.Users.ListRoles)
    }

    object User {
        data object Delete : EndpointDef("/user/delete")

        data object DeleteOther : EndpointDef("/user/{userId}", Roles.Users.Delete)

        data object GrantRole : EndpointDef("/user/grant", Roles.Users.GrantRole)

        data object List : EndpointDef("/user/list", Roles.Users.List)

        data object Meta : EndpointDef("/user/meta")

        data object MetaOther : EndpointDef("/user/meta/{userId}", Roles.Users.ModifyOthers)

        data object ProfileEdit : EndpointDef("/user/profile")

        data object Profile : EndpointDef("/user/profile")

        data object ProfileEditOther : EndpointDef("/user/profile/{userId}", Roles.Users.ModifyOthers)

        data object RevokeRole : EndpointDef("/user/revoke", Roles.Users.RevokeRole)

        object Transactions {
            data object Create : EndpointDef("/user/{userId}/transaction", Roles.Transaction.Create)

            data object Delete : EndpointDef("/transaction/{transactionId}", Roles.Transaction.Delete)

            data object List : EndpointDef("/user/transactions")

            data object ListOther : EndpointDef("/user/{userId}/transactions", Roles.Transaction.ListOthers)

            data object Update : EndpointDef("/transaction/{transactionId}", Roles.Transaction.Update)
        }
    }
}
