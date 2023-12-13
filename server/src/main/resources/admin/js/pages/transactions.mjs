import {getCache} from "../data-storage.js";
import {STORAGE_PROFILE, STORAGE_TOKEN} from "../const.js";
import {_} from "../utils.mjs";
import {get, post} from "../request.js";

/**
 * @typedef {Object} Transaction
 * @property {string} date
 * @property {string} description
 * @property {boolean} income
 * @property {number} units
 * @property {number} pricePerUnit
 * @property {string} type
 */

/**
 * @typedef {APIResult} TransactionsListResult
 * @property {boolean} success
 * @property {Object} data
 * @property {Transaction[]} data.transactions
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
let profile;

/**
 * @param {Event} event
 */
const onSubmitNewTransactionDialog = async function (event) {
    event.preventDefault();

    /** @type {HTMLInputElement} */
    const incomeField = _('transactionIncome');
    /** @type {HTMLInputElement} */
    const dateField = _('transactionDate');
    /** @type {HTMLSelectElement} */
    const typeField = _('transactionType');
    /** @type {HTMLTextAreaElement} */
    const descriptionField = _('transactionDescription');
    /** @type {HTMLInputElement} */
    const unitsField = _('transactionUnits');
    /** @type {HTMLInputElement} */
    const ppuField = _('transactionPPU');

    const income = incomeField.checked;
    const date = dateField.valueAsDate;
    const type = typeField.value;
    const description = descriptionField.value;
    const units = unitsField.valueAsNumber;
    const pricePerUnit = ppuField.valueAsNumber;

    try {
        const transactionsResult = await post(
            `/user/${profile.id}/transaction`,
            { date, description, income, units, pricePerUnit, type },
            _token
        );
        console.info('Result:', transactionsResult);

        window.location.reload();
    } catch (error) {
        console.error('Could not create transaction:', error);
    }
}

window.addEventListener('load', async function () {
    if (localStorage == null) {
        alert('Your device doesn\'t support localStorage.')
        return
    }

    _('newTransactionDialog').addEventListener('submit', onSubmitNewTransactionDialog);

    /** @type {string|null} */
    _token = localStorage.getItem(STORAGE_TOKEN);
    if (_token == null) {
        console.log("User not logged in, redirecting to root...");
        window.location.replace('/admin');
        return
    }

    _('loading_indicator').style.display = 'none';
    _('main_container').style.display = 'block';

    /** @type {ProfileData} */
    profile = getCache(STORAGE_PROFILE);
    const roles = profile.roles.map((role) => role.type);

    /** @type {TransactionsListResult} */
    const transactionsResult = await get('/user/transactions', _token);
    const transactions = transactionsResult.data.transactions;
    console.info('Transactions:', transactions);

    const transactionsList = _('transactionsList');
    for (const transaction of transactions) {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${transaction.date}</td>` +
            `<td>${transaction.type}</td>` +
            `<td>${transaction.description}</td>` +
            `<td>${transaction.units}</td>` +
            `<td>${transaction.pricePerUnit}</td>` +
            `<td>${transaction.units * transaction.pricePerUnit}</td>`;
        transactionsList.append(row);
    }
    if (transactions.length <= 0) {
        _('transactionsEmpty').style.display = 'block';
    }

    if (roles.includes('com.filamagenta.security.Roles.Transaction.Create')) {
        _('newTransactionButton').style.display = 'block';
    }
});
