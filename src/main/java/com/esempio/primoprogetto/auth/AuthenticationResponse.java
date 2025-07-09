package com.esempio.primoprogetto.auth;

// Semplice classe per la risposta JSON che conterrà il token
public class AuthenticationResponse {
    private String token;
    public AuthenticationResponse(String token) { this.token = token; }
    // Getter
    public String getToken() { return token; }
}