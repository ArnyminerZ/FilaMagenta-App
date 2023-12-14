import {getUsersList} from "../modules/users.mjs";

/**
 * Downloads the given user's meta-information, and loads it into a table with the given id.
 *
 * Note that it's assumed that the table already has a header row.
 *
 * Take into account that for using this function, the logged-in user must have the role
 * `com.filamagenta.security.Roles.Users.List`.
 *
 * @param {number} userId The id of the user to fetch the data for.
 * @param {string} tableId The id of the table to fill.
 *
 * @returns {Promise<void>}
 */
export async function loadUserMetaTable(userId, tableId) {
    const usersList = await getUsersList();
    const data = usersList.find((profile) => profile.id === userId);

    fillUserMetaTable(data, tableId)
}

/**
 * Given some `ProfileData`, fills the table desired with the user's meta-data.
 *
 * Note that it's assumed that the table already has a header row, and the contents of the table will be cleared before
 * loading the new ones.
 *
 * @param {ProfileData} data The data to load
 * @param {string} tableId The id of the table to fill.
 */
export function fillUserMetaTable(data, tableId) {
    const tableElement = document.getElementById(tableId);
    const rowElements = tableElement.getElementsByTagName('tr');
    /** @type {HTMLTableRowElement[]} */
    const dataRowElements = [...rowElements].slice(1);
    for (const row of dataRowElements) { row.remove() }

    /** @type {[string,string][]} */
    const meta = Object.entries(data.meta);
    for (const entry of meta) {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${entry[0]}</td>` +
            `<td>${entry[1]}</td>`;
        tableElement.append(row);
    }
}