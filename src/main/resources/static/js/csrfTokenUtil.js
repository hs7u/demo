/**
 * 注入csrf網頁元素
 * @param {Element} domes 需要被注入的網頁元素
 */
export function appendCsrfToken(...domes) {
        fetch("/csrfToken").then(res => {
            return res.json();
    }).then(json => {
            const csrfDom = document.createElement("input");
        csrfDom.setAttribute("type", "hidden");
        csrfDom.setAttribute("name", "_csrf");
        csrfDom.setAttribute("value", json.token);

        domes.forEach(dom => {
            dom.appendChild(csrfDom);
        });
    });
}


/**
 * post時攜帶csrf標頭
 * @param {string} url 要請求的網頁
 * @param {string} body 攜帶的資料
 */
export function postWithCsrfToken(url, body) {
        return fetch("/csrfToken").then(res => {
            return res.json();
    }).then(json => {
            return fetch(url, {
                method: "POST",
            headers: new Headers({
                    'Content-Type': 'application/json',
                [json.headerName]: json.token
            }),
            body: JSON.stringify(body)
        });
    });
}
