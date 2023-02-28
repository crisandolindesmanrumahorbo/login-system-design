package com.rumahorbo.login.controller;

import com.rumahorbo.login.model.LoginRequestDTO;
import com.rumahorbo.login.model.LoginResponseDTO;
import com.rumahorbo.login.model.LogoutRequestDTO;
import com.rumahorbo.login.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO login = this.authenticationService.login(loginRequestDTO);
        return new ResponseEntity<>(login, login == null ? HttpStatus.UNAUTHORIZED : HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        return new ResponseEntity<>(this.authenticationService.logout(logoutRequestDTO));
    }

    @GetMapping("/token")
    public ResponseEntity<?> verifyToken() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
