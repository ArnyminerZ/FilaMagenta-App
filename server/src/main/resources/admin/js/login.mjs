import {_} from './utils.mjs';
import {post} from './request.js';

const STORAGE_TOKEN = 'TOKEN';

/**
 * @typedef {APIResult} LoginSuccessResult
 * @property {boolean} success
 * @property {Object} data
 * @property {string} data.token
 */

window.addEventListener('load', function () {
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
    }
});
