/**
 * Retrieves data from the localStorage based on a given key.
 *
 * @param {string} key - The key associated with the data to be retrieved from localStorage.
 * @param {number} [expirationTime=3600000] - The expiration time for the cached data in milliseconds.
 * Default is 1 hour.
 *
 * @returns {Object|null} - The retrieved data from localStorage, or null if no data is found or if the data has expired.
 */
export function getCache(key, expirationTime = 60 * 60 * 1000) {
    const data = getCacheRaw(key, expirationTime);
    return JSON.parse(data);
}

/**
 * Retrieves data from the localStorage based on a given key.
 *
 * @param {string} key - The key associated with the data to be retrieved from localStorage.
 * @param {number} [expirationTime=3600000] - The expiration time for the cached data in milliseconds.
 * Default is 1 hour.
 *
 * @returns {Object|null} - The retrieved data from localStorage, or null if no data is found or if the data has expired.
 * Returns the data as stored, doesn't process it in any way.
 */
export function getCacheRaw(key, expirationTime = 60 * 60 * 1000) {
    /** @type {string|null} */
    const data = localStorage.getItem(key);
    if (data == null) return null;

    /** @type {string|null} */
    const lastUpdate = localStorage.getItem(`${key}_UPDATE`);
    if (lastUpdate == null) return null;

    const lastUpdateTime = new Date(lastUpdate);
    const now = new Date();
    const diff = Math.abs(now - lastUpdateTime);

    if (diff > expirationTime) {
        // Data has expired
        console.info('Cached data has expired, clearing and redirecting to root.');
        localStorage.removeItem(key);
        localStorage.removeItem(`${key}_UPDATE`);
        window.location.replace('/admin');
        return null;
    }

    return data;
}

/**
 * Sets the value of a key in the local storage along with the update timestamp.
 *
 * @param {string} key - The key to set the value for.
 * @param {Object} data - The data to be stored.
 */
export function setCache(key, data) {
    setCacheRaw(key, JSON.stringify(data));
}

/**
 * Sets the value of a key in the local storage along with the update timestamp.
 * Doesn't do any processing with the data, stores it as is.
 *
 * @param {string} key - The key to set the value for.
 * @param {string} data - The data to be stored.
 */
export function setCacheRaw(key, data) {
    localStorage.setItem(key, data);
    localStorage.setItem(`${key}_UPDATE`, (new Date()).toISOString())
}

/**
 * Removes the given key from the local storage.
 *
 * @param {string} key
 */
export function removeCache(key) {
    localStorage.removeItem(key);
    localStorage.removeItem(`${key}_UPDATE`);
}
