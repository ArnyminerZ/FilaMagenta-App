import {STORAGE_PROFILE, STORAGE_TOKEN} from "../const.js";
import {_} from "../utils.mjs";
import {getCache} from "../data-storage.js";
import {get} from "../request.js";

/**
 * @callback OnTokenObtained
 * @param {string} token
 */

/**
 * @callback OnProfileObtained
 * @param {ProfileData} profile
 */

/**
 * @callback OnListObtained
 * @template {APIResult} ResultType
 * @param {ResultType} result
 */

/**
 * @callback OnListLoadError
 * @param {APIError} error
 */

/**
 * Adds a listener when the window is loaded to prepare the UI.
 *
 * If the user is not logged in, redirects to root.
 *
 * @template {APIResult} ListResultType
 * @param {string} listEndpoint The endpoint to fetch for loading the list of `ListResultType`.
 * @param {Map<string, string>} showRoles values match ID of a DOM element to display if the user has the key role.
 * @param {OnTokenObtained} onTokenObtained Called when the token is obtained.
 * @param {OnProfileObtained} onProfileObtained Called when the user's profile is obtained.
 * @param {OnListObtained<ListResultType>} onListObtained Called when the list of `listEndpoint` is loaded.
 * @param {OnListLoadError} onListLoadError Called if there's any error while loading the contents of `listEndpoint`.
 */
export function prepare(
    listEndpoint,
    showRoles,
    onTokenObtained,
    onProfileObtained,
    onListObtained,
    onListLoadError
) {
    window.addEventListener('load', async function () {
        if (localStorage == null) {
            alert('Your device doesn\'t support localStorage.')
            return
        }

        /** @type {string|null} */
        const token = localStorage.getItem(STORAGE_TOKEN);
        if (token == null) {
            console.log("User not logged in, redirecting to root...");
            window.location.replace('/admin');
            return
        }
        onTokenObtained(token);

        _('loading_indicator').style.display = 'none';
        _('main_container').style.display = 'block';

        /** @type {ProfileData} */
        const profile = getCache(STORAGE_PROFILE);
        const roles = profile.roles.map((role) => role.type);
        onProfileObtained(profile);

        for (const pair of showRoles) {
            const [role, id] = pair;
            if (roles.includes(role)) {
                _(id).style.display = 'inline-block';
            }
        }

        try {
            /** @type {APIResult} */
            const result = await get(listEndpoint, token);
            onListObtained(result)
        } catch (/** @type {APIError} */ error) {
            console.error('Could not obtain the data list. Error:', error);
            onListLoadError(error);
        }
    });
}