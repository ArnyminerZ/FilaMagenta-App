import {_} from "../utils.mjs";
import {post} from "../request.mjs";
import {getToken} from "./auth.mjs";

/**
 * Works as a handler for forms that create new transactions. The list of IDs for the fields are:
 * - `transactionUserId`: A hidden field that holds the id of the user owner of the transaction.
 * - `transactionIncome`: A checkbox which indicates if the transaction is income (checked) or outcome (unchecked).
 * - `transactionDate`: A date field which holds the date when the transaction should match.
 * - `transactionType`: A select field that holds all the types of transactions in the options' values.
 * - `transactionDescription`: A textarea that contains the description of the transaction.
 * - `transactionUnits`: A numeric field that holds the number of units in the transaction. The value is never lower
 * or equal than 0.
 * - `transactionPPU`: A numeric field that holds the price per unit. Never lower than `0.01` (in euros).
 *
 * @param {SubmitEvent} event The event sent by the submitting form.
 */
export async function onSubmitNewTransactionDialog(event) {
    event.preventDefault();

    /** @type {HTMLInputElement} */
    const userIdField = _('transactionUserId');
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

    const userId = parseInt(userIdField.value);
    const income = incomeField.checked;
    const date = dateField.valueAsDate;
    const type = typeField.value;
    const description = descriptionField.value;
    const units = unitsField.valueAsNumber;
    const pricePerUnit = ppuField.valueAsNumber;

    try {
        const transactionsResult = await post(
            `/user/${userId}/transaction`,
            { date, description, income, units, pricePerUnit, type },
            await getToken()
        );
        console.info('Result:', transactionsResult);

        alert('Transaction created!');

        window.location.reload();
    } catch (error) {
        console.error('Could not create transaction:', error);
    }
}
