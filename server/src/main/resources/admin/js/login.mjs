import {_, CC} from './utils.mjs';
import {get, post} from './request.js';
import {getCache, setCache} from "./data-storage.js";
import {STORAGE_PROFILE, STORAGE_TOKEN} from "./const.js";

/**
 * @typedef {APIResult} LoginSuccessResult
 * @property {boolean} success
 * @property {Object} data
 * @property {string} data.token
 */

/**
 * @typedef {Object} Role
 * @property {string} type
 */

/**
 * @typedef {Object} ProfileData
 * @property {number} id
 * @property {string} name
 * @property {string} surname
 * @property {string} nif
 * @property {Role[]} roles
 * @property {Object} meta
 */

/**
 * @typedef {APIResult} ProfileSuccessResult
 * @property {boolean} success
 * @property {ProfileData} data
 */

/**
 * Fills the elements selected by the given class name with the provided contents.
 *
 * @param {string} className - The class name used to select the elements.
 * @param {string} contents - The contents to be inserted into the elements.
 */
function fill(className, contents) {
    const list = CC(className);
    for (const el of list) {
        el.innerText = contents;
    }
}

function refreshUI() {
    /** @type {ProfileData} */
    const profile = getCache(STORAGE_PROFILE);

    _('loading_indicator').style.display = 'none';
    _('main_container').style.display = 'block';

    fill('ma-fill-fullname', `${profile.name} ${profile.surname}`);
    fill('ma-fill-name', profile.name);
    fill('ma-fill-surname', profile.surname);
    fill('ma-fill-nif', profile.nif);

    // const metadataTable = _('metadata-table');
    // todo: fill metadata table

    const rolesList = _('roles-list');
    for (const role of profile.roles) {
        const item = document.createElement('li');
        item.innerText = role.type;
        rolesList.append(item);
    }
}

window.addEventListener('load', async function () {
    if (localStorage == null) {
        alert('Your device doesn\'t support localStorage.')
        return
    }

    _('login_form').addEventListener('submit', async function (ev) {
        ev.preventDefault()

        /** @type {HTMLInputElement} */
        const nifField = _('nif');
        /** @type {HTMLInputElement} */
        const passwordField = _('password');

        const nif = nifField.value;
        const password = passwordField.value;

        console.debug('Logging in as', nif, '...');

        try {
            /** @type {LoginSuccessResult} */
            const result = await post('/auth/login', {nif, password});
            const token = result.data.token;

            localStorage.setItem(STORAGE_TOKEN, token);

            window.location.reload();
        } catch (/** @type {APIError} */ error) {
            // TODO: show error to user
            console.error('Could not log in:', error);
        }
    })

    /** @type {string|null} */
    const token = localStorage.getItem(STORAGE_TOKEN);
    if (token == null) {
        console.log("User not logged in, showing login container...");
        _('loading_indicator').style.display = 'none';
        _('login_container').style.display = 'block';
    } else {
        console.info('User is logged in.');

        // todo: try-catch
        /** @type {ProfileSuccessResult} */
        const profileResult = await get('/user/profile', token);
        const profile = profileResult.data;

        setCache(STORAGE_PROFILE, profile);

        refreshUI();
    }
});
