import { useEffect, useState } from "react";

function AccountPage(){
    const api_domain = "http://localhost:8089";
    const [msg, setMsq] = useState('Загрузка...');

    useEffect(()=>{
        //maybe header Authorization?
        const searchParams = new URLSearchParams(window.location.search);
        const token = searchParams.get('token');
        const replaceurl = window.location.href.replace('?token='+token,'');
        window.history.replaceState('','',replaceurl);
        if (token){
            fetch(api_domain+'/oauth/login?token='+token,
                {
                    method: 'GET',
                    credentials: 'include'
                }
            ).then(response => {
                    console.log(response);
                    setMsq('Вход осуществлён!');
            });
        }
        else{
            setMsq('Вход был выполнен ранее.');
        }

        //запрос данных пользователя

    }, []);

    const onLogout = async function (){
        let logoutResponse = await fetch(api_domain+'/oauth/logout',
            {
                method: 'GET',
                credentials: 'include'
            }
        );
        //refresh token
        if (await logoutResponse.status===444){
            console.log("refresh...");
            let refreshResponse = await fetch(api_domain+'/oauth/refresh',
                {
                    method: 'GET',
                    credentials: 'include'
                }
            );
            if (await refreshResponse.ok){
                logoutResponse = await fetch(api_domain+'/oauth/logout',
                    {
                        method: 'GET',
                        credentials: 'include'
                    }
                );
            }
            else{
                console.error(await refreshResponse.text());
            }
        }

        if (await logoutResponse.ok){
            window.location.pathname = '/login';
        }
        else{
            console.error(await logoutResponse.text());
        }
    }

    return (
        <div>
            <h1>{msg}</h1>
            <header>
                {//img
                }
                <button onClick={onLogout}>Выйти</button>
            </header>
            
            <div>
                {//img
                }
                <button>Редактировать</button>
                <p>Фамилия</p>
                <p>Имя</p>
                <p>Отчество</p>
                <div>
                    <h2>Номера телефонов</h2>
                    <p>88005553535 - основной</p>
                    <p>88005553535</p>
                    <p>88005553535</p>
                </div>
                <div>
                    <h2>Адреса</h2>
                    <p>Пушкина Колотушкина - основной</p>
                    <p>Ленина Сталина</p>
                    <p>Мамина Папина</p>
                </div>
                <div>
                    <h2>Доступ доверенных сервисов</h2>
                    <p>Аккаунт</p>
                    <p>Студент</p>
                    <p>Преподаватель</p>
                </div>
                <div>
                    <h2>Открытые сессии</h2>
                    <p>Аккаунт - выйти</p>
                    <p>Студент - выйти</p>
                    <p>Преподаватель - выйти</p>
                </div>

            </div>
        </div>
    );
}

export default AccountPage;