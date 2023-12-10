/**
 * @typedef {Object} APIError
 * @property {boolean} success - will be false
 * @property {Object} error
 * @property {number} error.code
 * @property {string} error.message
 */

/**
 * @typedef {Object} APIResult
 * @property {boolean} success - will be true
 * @property {Object} data
 */

/**
 * Sends a POST request to the specified URL with the provided body.
 *
 * @param {string} url - The URL to send the POST request to.
 * @param {object} body - The body of the POST request. Should be a valid JSON object.
 * @returns {Promise<APIResult>} - A Promise that resolves with the response text if the request is successful, or rejects
 * with the error message if the request fails.
 */
export function post(url, body) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', url);
        xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(JSON.parse(xhr.responseText));
            } else {
                reject(JSON.parse(xhr.responseText));
            }
        };
        xhr.send(
            JSON.stringify(body)
        );
    });
}
