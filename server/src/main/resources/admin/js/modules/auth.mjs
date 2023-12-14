import {STORAGE_TOKEN} from "../const.mjs";

/**
 * Tries to get the current token stored in the local storage.
 *
 * If there's no token stored, the user is redirected to `/admin` automatically; when this happens, `null` is returned.
 * However, it can be safely assumed that this function never returns null.
 *
 * @returns {string|null}
 */
export function getToken() {
    /** @type {string|null} */
    const token = localStorage.getItem(STORAGE_TOKEN);
    if (token == null) {
        console.log("User not logged in, redirecting to root...");
        window.location.replace('/admin');
        return null;
    } else {
        return token;
    }
}
