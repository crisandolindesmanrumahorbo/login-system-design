package com.rumahorbo.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumahorbo.login.model.LoginRequestDTO;
import com.rumahorbo.login.model.LoginResponseDTO;
import com.rumahorbo.login.model.LogoutRequestDTO;
import com.rumahorbo.login.service.RestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.BodyInserters;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestService restService;

    @Test
    void login_shouldReturn401HTTPStatus_whenUserNotValid() throws Exception {
        final String username = "cris";
        final String password = "P@ssw0rd";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(username, password);
        String login = this.objectMapper.writeValueAsString(loginRequestDTO);
        Mockito.when(this.restService.post(any(String.class), any(BodyInserters.FormInserter.class), eq(LoginResponseDTO.class))).thenReturn(null);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/authentication/login")
                .content(login)
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnToken_whenUserValid() throws Exception {
        final String username = "cris";
        final String password = "P@ssw0rd";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(username, password);
        String requestString = this.objectMapper.writeValueAsString(loginRequestDTO);
        final String token = "";
        final String type = "Bearer";
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, type, "");
        String expectedJson = this.objectMapper.writeValueAsString(loginResponseDTO);
        Mockito.when(this.restService.post(anyString(), any(BodyInserters.FormInserter.class), eq(LoginResponseDTO.class))).thenReturn(loginResponseDTO);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/authentication/login")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser
    void logout_shouldReturnHttpStatusOK_whenUserAlreadyLogin() throws Exception {
        final String refresh_token = "";
        LogoutRequestDTO logoutRequestDTO = new LogoutRequestDTO(refresh_token);
        String requestString = this.objectMapper.writeValueAsString(logoutRequestDTO);
        Mockito.when(this.restService.logout(anyString(), any(BodyInserters.FormInserter.class), anyString(), anyString())).thenReturn(HttpStatus.OK);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/authentication/logout")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void logout_shouldReturnHttpStatusUnAuthorized_whenTokenInvalid() throws Exception {
        final String refresh_token = "";
        LogoutRequestDTO logoutRequestDTO = new LogoutRequestDTO(refresh_token);
        String requestString = this.objectMapper.writeValueAsString(logoutRequestDTO);
        Mockito.when(this.restService.logout(anyString(), any(BodyInserters.FormInserter.class), anyString(), anyString())).thenReturn(HttpStatus.OK);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/authentication/logout")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void token_shouldReturnHttpStatusOK_whenTokenValid() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/authentication/token")
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void token_shouldReturnHttpStatusUnAuthorized_whenTokenInValid() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/authentication/token")
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request)
                .andExpect(status().isUnauthorized());
    }

}
