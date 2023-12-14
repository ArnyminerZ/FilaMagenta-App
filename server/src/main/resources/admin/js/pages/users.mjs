import {prepare} from "./pages.mjs";
import {_} from "../utils.mjs";

/**
 * @typedef {APIResult} UsersListResult
 * @property {boolean} success
 * @property {Object} data
 * @property {ProfileData[]} data.users
 */

/**
 * Holds the authentication token of the current user.
 * @private
 * @type {string}
 */
let _token;

/**
 * Holds the currently logged-in user's data in memory to be used at any point.
 * @private
 * @type {ProfileData}
 */
let _profile;

prepare(
    '/user/list',
    new Map(),
    (token) => { _token = token },
    (profile) => { _profile = profile },
    (/** @type {UsersListResult} */ list) => {
        console.log('Result:', list.data);
        const users = list.data.users;

        const domList = _('usersList');
        for (const item of users) {
            const row = document.createElement('tr');
            row.innerHTML = `<td>${item.id}</td>` +
                `<td>${item.nif}</td>` +
                `<td>${item.name}</td>` +
                `<td>${item.surname}</td>` +
                `<td>${item.id === _profile.id ? 'You' : ''}</td>`;
            domList.append(row);
        }
        if (users.length <= 0) {
            _('usersList').style.display = 'block';
        }
    },
    (error) => { alert('Could not load users.') }
);
