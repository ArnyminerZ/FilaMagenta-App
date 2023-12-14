/**
 * Alias for `document.getElementById`.
 * @alias document.getElementById
 * @param {string} id The id of the element to fetch.
 * @returns {HTMLElement|null}
 */
export function _(id) {
    return document.getElementById(id);
}

/**
 * Alias for `document.getElementById`.
 * @alias document.getElementsByName
 * @param {string} className The class name of the element to fetch.
 * @returns {NodeListOf<HTMLElement>|null}
 */
export function CC(className) {
    return document.getElementsByClassName(className);
}
