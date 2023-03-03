package com.rumahorbo.login.service;

import com.rumahorbo.login.annotation.Log;
import com.rumahorbo.login.constant.Constant;
import com.rumahorbo.login.model.LoginRequestDTO;
import com.rumahorbo.login.model.LoginResponseDTO;
import com.rumahorbo.login.model.LogoutRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

@Service
public class AuthenticationService {

    @Autowired
    private RestService restService;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    @Value("${KEYCLOAK_GRANT_TYPE}")
    private String grantType;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${KEYCLOAK_URL_ACCESS_TOKEN}")
    private String accessTokenUrl;

    @Value("${KEYCLOAK_URL_LOGOUT}")
    private String logoutUrl;

    @Log
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        BodyInserters.FormInserter<String> loginKeycloakRequest = this.generateKeycloakRequestToken(loginRequestDTO);
        return this.restService.post(accessTokenUrl, loginKeycloakRequest, LoginResponseDTO.class);
    }

    private BodyInserters.FormInserter<String> generateKeycloakRequestToken(LoginRequestDTO loginRequestDTO) {
        return BodyInserters.fromFormData
                        (Constant.KEYCLOAK_USERNAME, loginRequestDTO.username())
                .with(Constant.KEYCLOAK_PASSWORD, loginRequestDTO.password())
                .with(Constant.KEYCLOAK_GRANT_TYPE, grantType)
                .with(Constant.KEYCLOAK_CLIENT_ID, clientId)
                .with(Constant.KEYCLOAK_CLIENT_SECRET, clientSecret);
    }

    private BodyInserters.FormInserter<String> generateRequestBodyLogout(LogoutRequestDTO logoutRequestDTO) {
        return BodyInserters.fromFormData
                        (Constant.KEYCLOAK_REFRESH_TOKEN, logoutRequestDTO.refresh_token());
    }

    public HttpStatus logout(LogoutRequestDTO logoutRequestDTO) {
        BodyInserters.FormInserter<String> logoutRequestBody = this.generateRequestBodyLogout(logoutRequestDTO);
        return this.restService.logout(logoutUrl, logoutRequestBody, clientId, clientSecret);
    }
}
