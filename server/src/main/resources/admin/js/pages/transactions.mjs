import {_} from "../utils.mjs";
import {post} from "../request.mjs";
import {prepare} from "./pages.mjs";

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
let _profile;

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
            `/user/${_profile.id}/transaction`,
            { date, description, income, units, pricePerUnit, type },
            _token
        );
        console.info('Result:', transactionsResult);

        window.location.reload();
    } catch (error) {
        console.error('Could not create transaction:', error);
    }
}

prepare(
    '/user/transactions',
    new Map(
        [
            ['com.filamagenta.security.Roles.Transaction.Create', 'newTransactionButton'],
            ['com.filamagenta.security.Roles.Users.List', 'navbar_users']
        ]
    ),
    (token) => { _token = token; },
    (profile) => { _profile = profile; },
    (/** @type {TransactionsListResult} */ list) => {
        const transactions = list.data.transactions;

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

        _('newTransactionDialog').addEventListener('submit', onSubmitNewTransactionDialog);
    },
    () => { alert('Could not load transactions.') }
);
