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
// 登入H2 console + query時
// appendCsrfToken(document.getElementById("login"));
// appendCsrfToken(document.querySelector("[name=h2querysubmit]"));

// example
// let gqlBody = {
//     query: `mutation($empId: ID!, $empName: String!, $password: String!, $newPassword: String!) {
//         updatePassword(empId: $empId,empName: $empName, password: $password, newPassword: $newPassword){                        
//                 empId
//                 empName
//                 password
//                 empRoles{
//                     empId
//                     roleId
//                     role {
//                         roleId
//                         roleName
//                         description
//                     }
//                 }
            
//         }
//     }`,
//     variables: {
//         empId : "18beb343-4260-4c41-8d91-45492cf9cc07",
//         empName: "test",
//         password: 1234,
//         newPassword: 2234
//     }
// }            