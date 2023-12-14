import {prepare} from "./pages.mjs";
import {_} from "../utils.mjs";
import {httpDelete, post} from "../request.js";
import {USER_IMMUTABLE} from "../const/errors.js";

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

async function removeUser(id) {
    try {
        /** @type {APIResult} */
        const result = await httpDelete(`/user/${id}`, null, _token);
        console.info('User deleted correctly:', result);

        window.location.reload()
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

window.removeUser = removeUser;

async function onSubmitUserCreateDialog(event) {
    event.preventDefault();

    /** @type {HTMLInputElement} */
    const nifField = _('userNIF');
    /** @type {HTMLInputElement} */
    const nameField = _('userName');
    /** @type {HTMLInputElement} */
    const surnameField = _('userSurname');
    /** @type {HTMLInputElement} */
    const passwordField = _('userPassword');

    const nif = nifField.value;
    const name = nameField.value;
    const surname = surnameField.value;
    const password = passwordField.value;

    try {
        await post('/auth/register', {nif, name, surname, password}, _token);

        window.location.reload();
    } catch (/** @type {APIError} */ error) {
        alert(`Could not create user. Error: ${error.error.message}`);
        console.error('Could not delete user:', error);
    }
}

prepare(
    '/user/list',
    new Map(
        [
            ['com.filamagenta.security.Roles.Users.Create', 'newUserButton']
        ]
    ),
    (token) => {
        _token = token
    },
    (profile) => {
        _profile = profile
    },
    (/** @type {UsersListResult} */ list) => {
        console.log('Result:', list.data);
        const users = list.data.users;

        const domList = _('usersList');
        for (const item of users) {
            const row = document.createElement('tr');
            const removeButton = `<button onclick="if (confirm('Are you sure?')) removeUser(${item.id})">Remove</button>`;
            row.innerHTML = `<td>${item.id}</td>` +
                `<td>${item.nif}</td>` +
                `<td>${item.name}</td>` +
                `<td>${item.surname}</td>` +
                `<td>${item.id === _profile.id ? 'You' : ''}</td>` +
                `<td>${removeButton}<button>Metadata</button></td>`;
            domList.append(row);
        }
        if (users.length <= 0) {
            _('usersList').style.display = 'block';
        }

        _('newUserForm').addEventListener('submit', onSubmitUserCreateDialog);
    },
    (error) => {
        alert('Could not load users.')
    }
);
