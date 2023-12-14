import {prepare} from "./pages.mjs";
import {_} from "../utils.mjs";
import {get, httpDelete, post} from "../request.js";
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

/**
 * Holds the data of all the users in memory.
 * @private
 * @type {ProfileData[]}
 */
let _usersList;

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

/**
 * Shows the user's metadata dialog after having loaded it with the data of the user with id `userId`.
 * @param {number} userId
 * @returns {Promise<void>}
 */
async function showMetadata(userId) {
    _('metadataUserIdField').value = userId;

    await loadUserMetadata(userId);

    /** @type {HTMLDialogElement} */
    const dialog = _('metadataDialog');
    dialog.showModal();
}

/**
 * Loads the given user's metadata into the metadata dialog (`#metadataTable`).
 * @param {number} userId The id of the user to load the data from.
 * @returns {Promise<void>}
 */
async function loadUserMetadata(userId) {
    await reloadUsersList();

    const tableElement = document.getElementById('metadataTable');
    const rowElements = tableElement.getElementsByTagName('tr');
    /** @type {HTMLTableRowElement[]} */
    const dataRowElements = [...rowElements].slice(1);
    for (const row of dataRowElements) { row.remove() }

    const data = _usersList.find((profile) => profile.id === userId);
    /** @type {[string,string][]} */
    const meta = Object.entries(data.meta);
    for (const entry of meta) {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${entry[0]}</td>` +
            `<td>${entry[1]}</td>`;
        tableElement.append(row);
    }
    console.info('Meta:', data);
}

/**
 * Fetches the server and updates the value of `_usersList`.
 * @returns {Promise<void>}
 */
async function reloadUsersList() {
    /** @type {UsersListResult} */
    const result = await get('/user/list', _token);
    _usersList = result.data.users;
}

window.removeUser = removeUser;
window.showMetadata = showMetadata;

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

async function onSubmitMetadataAddForm(event) {
    event.preventDefault();

    /** @type {HTMLInputElement} */
    const userIdField = _('metadataUserIdField');
    /** @type {HTMLSelectElement} */
    const keyField = _('metadataKeyField');
    /** @type {HTMLInputElement} */
    const valueField = _('metadataValueField');

    const userId = parseInt(userIdField.value);
    const key = keyField.value;
    const value = valueField.value;

    try {
        await post(`/user/meta/${userId}`, {key, value}, _token);

        await loadUserMetadata(userId);
    } catch (/** @type {APIError} */ error) {
        alert(`Could not store metadata. Error: ${error.error.message}`);
        console.error('Could not store metadata:', error);
    }
}

prepare(
    '/user/list',
    new Map(
        [
            ['com.filamagenta.security.Roles.Users.Create', 'newUserButton'],
            ['com.filamagenta.security.Roles.Users.List', ['navbar_users', 'addMetadataForm']]
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
        _usersList = users;

        const domList = _('usersList');
        for (const item of users) {
            const row = document.createElement('tr');
            const removeButton = `<button onclick="if (confirm('Are you sure?')) removeUser(${item.id})">Remove</button>`;
            const metadataButton = `<button onclick="showMetadata(${item.id})">Metadata</button>`;
            row.innerHTML = `<td>${item.id}</td>` +
                `<td>${item.nif}</td>` +
                `<td>${item.name}</td>` +
                `<td>${item.surname}</td>` +
                `<td>${item.id === _profile.id ? 'You' : ''}</td>` +
                `<td>${removeButton}${metadataButton}</td>`;
            domList.append(row);
        }
        if (users.length <= 0) {
            _('usersList').style.display = 'block';
        }

        _('newUserForm').addEventListener('submit', onSubmitUserCreateDialog);
        _('addMetadataForm').addEventListener('submit', onSubmitMetadataAddForm);
    },
    (error) => {
        alert('Could not load users.')
    }
);
