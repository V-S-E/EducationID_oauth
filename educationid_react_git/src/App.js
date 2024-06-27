import './App.css';
import AccountPage from './pages/account/AccountPage';
import LoginPage from './pages/login/LoginPage';
import {BrowserRouter, Routes, Route} from 'react-router-dom'

function App(props) {
  return (
    <BrowserRouter>
      <div>
        <Routes>
          <Route path='/login' element={<LoginPage/>}/>
          <Route path='/account' element={<AccountPage/>}/>
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
