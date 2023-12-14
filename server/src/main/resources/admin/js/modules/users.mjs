import {getToken} from "./auth.mjs";
import {get, httpDelete, post} from "../request.mjs";
import {USER_IMMUTABLE} from "../const/errors.mjs";

/**
 * Fetches the server for the list of users.
 *
 * The logged-in user must have the `com.filamagenta.security.Roles.Users.List` role.
 * @returns {Promise<ProfileData[]>}
 */
export async function getUsersList() {
    /** @type {UsersListResult} */
    const result = await get('/user/list', await getToken());
    return result.data.users;
}

/**
 * Removes the user with the given ID.
 *
 * The logged-in user must have the `com.filamagenta.security.Roles.Users.Delete` role.
 *
 * If the user is removed successfully, and `reload` is `true`, the current location is refreshed.
 *
 * @param {number} id The id of the user to remove.
 * @param {boolean} reload If true, the window will be reloaded after removing the user.
 *
 * @returns {Promise<void>}
 */
export async function removeUser(id, reload = true) {
    try {
        /** @type {APIResult} */
        const result = await httpDelete(`/user/${id}`, null, await getToken());
        console.info('User deleted correctly:', result);

        if (reload) {
            window.location.reload();
        }
    } catch (/** @type {APIError} */ error) {
        switch (error.error.code) {
            case USER_IMMUTABLE:
                alert('Tried to delete an immutable user.');
                break;
            default:
                alert('Could not delete user');
                console.error('Could not delete user:', error);
                break;
        }
    }
}

/**
 * Tries to update the meta of the user with the given key, setting the desired value.
 *
 * If an error occurs, `false` is returned and an alert dialog is displayed.
 *
 * If updating others, the `com.filamagenta.security.Roles.Users.ModifyOthers` role is required.
 *
 * @param {number|null} userId If null is given, the current user will be updated. Otherwise, the user with the id given
 * will be updated.
 * @param {string} key
 * @param {string} value
 * @returns {Promise<boolean>} `true` if the metadata was updated correctly, `false` otherwise.
 */
export async function setUserMeta(userId, key, value) {
    try {
        if (!userId) {
            await post(`/user/meta`, {key, value}, await getToken());
        } else {
            await post(`/user/meta/${userId}`, {key, value}, await getToken());
        }

        return true;
    } catch (/** @type {APIError} */ error) {
        alert(`Could not store metadata. Error: ${error.error.message}`);
        console.error('Could not store metadata:', error);

        return false;
    }
}

window.removeUser = removeUser;
