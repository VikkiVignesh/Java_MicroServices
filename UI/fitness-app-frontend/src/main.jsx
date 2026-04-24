import React from 'react'
import ReactDOM from 'react-dom/client'

import { Provider } from 'react-redux'
import {store} from './store/store'
import {oauthConfig} from './config/oauthConfig'
import { AuthProvider } from 'react-oauth2-code-pkce'

import App from './App'

// As of React 18
const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(

  <AuthProvider authConfig={oauthConfig}
  loadingComponent={<div>Loading...</div>}
  errorComponent={<div>Authentication Error</div>}
  autoRefreshToken={true}
  >
        <Provider store={store}>
    <App />
  </Provider>
  </AuthProvider>
)