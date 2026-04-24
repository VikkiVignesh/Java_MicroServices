import './App.css'
import {BrowserRouter as Router, Navigate,Route,Routes,useLocation} from "react-router";
import Button from "@mui/material/Button";
import { useContext, useEffect } from 'react';
import {AuthContext} from "react-oauth2-code-pkce";
import {useDispatch} from "react-redux";
import { useState } from 'react';
import { setCredentials } from './store/slices/authSlice';
import Box from '@mui/material/Box';
import ActivityForm from './components/ActivityForm';
import Activitylist from './components/Activitylist';
import { Activity } from 'react';
import ActivityDetails from './components/ActivityDetails';

function ActivitesPage() {
  return (
   <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
    <p>Activities</p>
      <ActivityForm  onActivitySubmitted={() => { window.location.reload()}} />
      <Activitylist />
    </Box>
  );
}

function App() {

  const {token,tokenData,logIn,logOut,isAuthenticated}=useContext(AuthContext);

  const dispatch=useDispatch();

  const [authReady,setAuthReady]=useState(false);

  useEffect(()=>{
    if(token)
    {
      dispatch(setCredentials({token,user:tokenData}));
      setAuthReady(true);
    }
  },[token,tokenData,dispatch])
  return (
    <Router>

      {!token ? 
     <Button variant="contained" onClick={()=>{
      logIn()
     }} color="primary">
        Login
     </Button>:

     <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      <Routes>
        <Route path="/" element={token ? <Navigate to="/activities" /> : <div>Welcome please login</div>} />
        <Route path="/activities" element={<ActivitesPage />} />
        <Route path="/activities/:id" element={<ActivityDetails/>} />
      </Routes>
    </Box>

     }
    </Router>
  )
}

export default App
