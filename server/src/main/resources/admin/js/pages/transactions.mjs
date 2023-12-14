import {_} from "../utils.mjs";
import {prepare} from "./pages.mjs";
import {onSubmitNewTransactionDialog} from "../modules/transactions.js";

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

prepare(
    '/user/transactions',
    new Map(
        [
            ['com.filamagenta.security.Roles.Transaction.Create', 'newTransactionButton'],
            ['com.filamagenta.security.Roles.Users.List', 'navbar_users']
        ]
    ),
    (token) => {
        _token = token;
    },
    (profile) => {
        _profile = profile;
        _('transactionUserId').value = profile.id;
    },
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
    () => {
        alert('Could not load transactions.')
    }
);
