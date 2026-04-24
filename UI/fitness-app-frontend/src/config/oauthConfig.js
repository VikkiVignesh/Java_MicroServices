export const oauthConfig= {
  clientId: 'oauth2-pkce-client',
  authorizationEndpoint: 'http://localhost:1947/realms/fitness-OAuth2/protocol/openid-connect/auth',
  tokenEndpoint: 'http://localhost:1947/realms/fitness-OAuth2/protocol/openid-connect/token',
  redirectUri: 'http://localhost:5173/',
  scope: 'openid profile email offline_access',
  pkce: true,
  onRefreshTokenExpire: (event) => event.logIn(),
}