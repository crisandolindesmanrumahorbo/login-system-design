package com.rumahorbo.login.model;


public record LoginResponseDTO(String access_token, String token_type, String refresh_token) {
}
