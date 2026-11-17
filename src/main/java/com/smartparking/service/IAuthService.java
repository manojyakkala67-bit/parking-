package com.smartparking.service;

import com.smartparking.model.Admin;

/**
 * Service interface for Admin authentication and session management.
 */
public interface IAuthService {
    Admin login(String username, String password);
    void logout();
    Admin getCurrentAdmin();
    boolean isAuthenticated();
    boolean changePassword(int adminId, String oldPassword, String newPassword);
}
