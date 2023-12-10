import {getCache} from "../data-storage.js";
import {STORAGE_PROFILE, STORAGE_TOKEN} from "../const.js";
import {_} from "../utils.mjs";
import {get} from "../request.js";

window.addEventListener('load', async function () {
    if (localStorage == null) {
        alert('Your device doesn\'t support localStorage.')
        return
    }

    /** @type {string|null} */
    const token = localStorage.getItem(STORAGE_TOKEN);
    if (token == null) {
        console.log("User not logged in, redirecting to root...");
        window.location.replace('/admin');
        return
    }

    _('loading_indicator').style.display = 'none';
    _('main_container').style.display = 'block';

    /** @type {ProfileData} */
    const profile = getCache(STORAGE_PROFILE);

    const transactionsResult = await get('/user/transactions', token);
    const transactions = transactionsResult.data;
    console.info('Transactions:', transactions);
});
