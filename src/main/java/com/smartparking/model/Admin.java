package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Model class representing an Administrator user.
 */
public class Admin {
    private int adminId;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public Admin() {
        this.role = "ADMIN";
        this.createdAt = LocalDateTime.now();
    }

    public Admin(String username, String passwordHash, String salt, String fullName, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.role = role != null ? role : "ADMIN";
        this.createdAt = LocalDateTime.now();
    }

    public Admin(int adminId, String username, String passwordHash, String salt, 
                 String fullName, String email, String role, 
                 LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
