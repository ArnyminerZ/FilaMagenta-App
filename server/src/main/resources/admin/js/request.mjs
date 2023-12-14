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
 * Runs an HTTP request with the desired method.
 *
 * @private
 * @param {'GET'|'POST'|'PATCH'|'DELETE'} method The HTTP method to use
 * @param {string} url The endpoint to make the request to.
 * @param {Document|XMLHttpRequestBodyInit|Object|null} body If any, the body of the request.
 * @param {[[string,string]]} headers A list of headers to append to the request.
 * @returns {Promise<APIResult>}
 */
function httpRequest(method, url, body = null, headers = []) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url);
        for (let pair of headers) {
            const name = pair[0];
            const value = pair[1];
            xhr.setRequestHeader(name, value);
        }
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(JSON.parse(xhr.responseText));
            } else {
                reject(JSON.parse(xhr.responseText));
            }
        };
        if (body != null) {
            xhr.send(JSON.stringify(body));
        } else {
            xhr.send();
        }
    });
}

export function get(url, token = null) {
    return httpRequest('GET', url, null, [['Authorization', `Bearer ${token}`]]);
}

/**
 * Sends a POST request to the specified URL with the provided body.
 *
 * @param {string} url The URL to send the POST request to.
 * @param {Document|XMLHttpRequestBodyInit|Object|null} body The body of the POST request. Should be a valid JSON object.
 * @param {string|null} token If any, the authorization token to use for accessing the endpoint.
 * @returns {Promise<APIResult>} A Promise that resolves with the response text if the request is successful, or rejects
 * with the error message if the request fails.
 */
export function post(url, body = null, token = null) {
    return httpRequest(
        'POST',
        url,
        body,
        [['Content-Type', 'application/json; charset=UTF-8'], ['Authorization', `Bearer ${token}`]]
    );
}

/**
 * Sends a DELETE request to the specified URL with the provided body.
 *
 * @param {string} url The URL to send the POST request to.
 * @param {Document|XMLHttpRequestBodyInit|Object|null} body The body of the POST request. Should be a valid JSON object.
 * @param {string|null} token If any, the authorization token to use for accessing the endpoint.
 * @returns {Promise<APIResult>} A Promise that resolves with the response text if the request is successful, or rejects
 * with the error message if the request fails.
 */
export function httpDelete(url, body = null, token = null) {
    return httpRequest(
        'DELETE',
        url,
        body,
        [['Content-Type', 'application/json; charset=UTF-8'], ['Authorization', `Bearer ${token}`]]
    );
}
