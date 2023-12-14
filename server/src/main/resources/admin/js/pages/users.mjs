import {prepare} from "./pages.mjs";
import {_} from "../utils.mjs";
import {post} from "../request.mjs";
import {loadUserMetaTable} from "../dom/users.mjs";
import {setUserMeta} from "../modules/users.mjs";

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

/**
 * Shows the user's metadata dialog after having loaded it with the data of the user with id `userId`.
 * @param {number} userId
 * @returns {Promise<void>}
 */
async function showMetadata(userId) {
    _('metadataUserIdField').value = userId;

    await loadUserMetaTable(userId, 'metadataTable');

    /** @type {HTMLDialogElement} */
    const dialog = _('metadataDialog');
    dialog.showModal();
}

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

    const success = await setUserMeta(userId, key, value);
    if (success) {
        await loadUserMetaTable(userId, 'metadataTable');
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
    () => {
        alert('Could not load users.')
    }
);
