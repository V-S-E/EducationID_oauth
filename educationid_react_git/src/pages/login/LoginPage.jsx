import { useState } from "react";
import { SHA256 } from "crypto-js";
import encBase64 from "crypto-js/enc-base64";
import logo from "./Logo.png"
import styles from "./LoginPage.module.css"

function LoginPage() {

    //state
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');

    return (
        <div className={styles.centered}>
            <div className={styles.formCard}>
                <figure className={styles.imgContainer}>
                    <img src={logo} alt='logo'></img>
                </figure>
                <div className={styles.logPassContainer}>
                    <label for='loginField' className={styles.label}>Логин</label>
                    <input type='text' name='loginField' value={login}
                        onChange={(e) => setLogin(e.target.value)}
                        className={styles.inputField}/>
                </div>
                <div className={styles.logPassContainer}>
                    <label for='passwordField' className={styles.label}>Пароль</label>
                    <input type='password' name='passwordField' value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className={styles.inputField} />
                </div>
                <input type='button' name='accept' value={'Войти'}
                    onClick={(e) => { onInClick(login, password) }}></input>
                <br></br>
                <input type='button' name='reg' value={'Зарегистрироваться'}></input>
            </div>
        </div>
    );
}

async function onInClick(login, password) {
    //api_domain
    const api_domain = "http://localhost:8089";
    //get user salt
    const get_salt = await fetch(api_domain + '/authentication/get-salt?login=' + login,
        {
            method: "GET"
        }
    );
    let salt = "";
    if (get_salt.ok) {
        salt = await get_salt.text();
    }
    const encodeInput = salt + password;
    const passHash = SHA256(encodeInput).toString(encBase64);
    const currentDomain = window.location.origin;
    //get jwt and refresh
    let redirect_token = await fetch(api_domain + '/authentication/login-token',
        {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                "Refer": currentDomain
            },
            body: JSON.stringify({
                login: login,
                phone: "",
                passwordHash: passHash
            })
        }
    );
    if (redirect_token.redirected) {
        console.log("ok, token!");
        window.location.href = await redirect_token.url;
    }
    else{
        console.error(await redirect_token.text());
    }
}

export default LoginPage;