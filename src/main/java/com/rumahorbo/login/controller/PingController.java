package com.rumahorbo.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping("/user")
    public ResponseEntity<String> pingUser() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<String> pingAdmin() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
