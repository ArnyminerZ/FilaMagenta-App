import {_, CC} from './utils.mjs';
import {get, post} from './request.mjs';
import {getCache, getCacheRaw, removeCache, setCache, setCacheRaw} from "./data-storage.mjs";
import {STORAGE_PROFILE, STORAGE_TOKEN} from "./const.mjs";
import {TOKEN_EXPIRED} from './const/errors.mjs';
import {fillUserMetaTable} from "./dom/users.mjs";
import {setUserMeta} from "./modules/users.mjs";
import {getToken} from "./modules/auth.mjs";

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

    fillUserMetaTable(profile, 'metadataTable');

    const rolesList = _('roles-list');
    for (const role of profile.roles) {
        const item = document.createElement('li');
        item.innerText = role.type;
        rolesList.append(item);
    }
}

async function onSubmitMetadataForm(event) {
    event.preventDefault();

    /** @type {HTMLSelectElement} */
    const keyField = _('metadataKeyField');
    /** @type {HTMLInputElement} */
    const valueField = _('metadataValueField');

    const key = keyField.value;
    const value = valueField.value;

    const success = await setUserMeta(null, key, value);
    if (success) {
        /** @type {ProfileSuccessResult} */
        const profileResult = await get('/user/profile', await getToken());
        const profileData = profileResult.data;

        await fillUserMetaTable(profileData, 'metadataTable');

        keyField.value = null;
        valueField.value = null;
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

            setCacheRaw(STORAGE_TOKEN, token);

            window.location.reload();
        } catch (/** @type {APIError} */ error) {
            // TODO: show error to user
            console.error('Could not log in:', error);
        }
    })

    /** @type {string|null} */
    const token = getCacheRaw(STORAGE_TOKEN);
    if (token == null) {
        console.log("User not logged in, showing login container...");
        _('loading_indicator').style.display = 'none';
        _('login_container').style.display = 'block';
    } else {
        console.info('User is logged in.');

        // todo: try-catch
        try {
            /** @type {ProfileSuccessResult} */
            const profileResult = await get('/user/profile', token);
            const profile = profileResult.data;

            setCache(STORAGE_PROFILE, profile);

            const usersListRole = profile.roles
                .find((role) => role.type === 'com.filamagenta.security.Roles.Users.List');
            if (usersListRole != null) {
                _('navbar_users').style.display = 'inline-block';
            }

            _('addMetadataForm').addEventListener('submit', onSubmitMetadataForm);

            refreshUI();
        } catch (/** @type {APIError} */ error) {
            switch (error.error.code) {
                case TOKEN_EXPIRED:
                    console.error('Server returned error code', TOKEN_EXPIRED);
                    alert('Your login has expired, please, log in again.');

                    removeCache(STORAGE_TOKEN);

                    window.location.reload();
                    break;

                default:
                    console.error('Unknown error occurred while trying to fetch the data from the server:', error);
                    break;
            }
        }
    }
});
