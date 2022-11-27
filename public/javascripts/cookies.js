function setCookie(key, value, secondsToExpire) {
    const expirationDate = new Date();
    expirationDate.setTime(expirationDate.getTime() + (secondsToExpire*1000));
    let expires = "expires="+ expirationDate.toUTCString();
    document.cookie = key + "=" + value + ";" + expires + ";path=/";
}

function getCookie(key) {
    let name = key + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let cookies = decodedCookie.split(';');
    for(let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1);
        }
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return "";
}