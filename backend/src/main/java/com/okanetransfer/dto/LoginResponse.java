package com.okanetransfer.dto;

public class LoginResponse {
    private Long id;
    private String token;
    private String fullName;
    private String role;

    public LoginResponse(Long id, String token, String fullName, String role) {
        this.id = id;
        this.token = token;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}