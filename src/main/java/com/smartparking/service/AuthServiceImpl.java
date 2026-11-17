package com.smartparking.service;

import com.smartparking.dao.AdminDAOImpl;
import com.smartparking.dao.IAdminDAO;
import com.smartparking.model.Admin;
import com.smartparking.util.InvalidCredentialsException;
import com.smartparking.util.PasswordUtil;
import com.smartparking.util.ValidationException;

import java.util.Optional;

/**
 * Implementation of IAuthService for Admin authentication.
 */
public class AuthServiceImpl implements IAuthService {

    private final IAdminDAO adminDAO;
    private static Admin currentAdmin = null;

    public AuthServiceImpl() {
        this.adminDAO = new AdminDAOImpl();
    }

    public AuthServiceImpl(IAdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @Override
    public Admin login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }

        Optional<Admin> opt = adminDAO.findByUsername(username.trim());
        if (opt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        Admin admin = opt.get();
        boolean valid = PasswordUtil.verifyPassword(password, admin.getSalt(), admin.getPasswordHash());
        if (!valid) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        adminDAO.updateLastLogin(admin.getAdminId());
        currentAdmin = admin;
        return admin;
    }

    @Override
    public void logout() {
        currentAdmin = null;
    }

    @Override
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    @Override
    public boolean isAuthenticated() {
        return currentAdmin != null;
    }

    @Override
    public boolean changePassword(int adminId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        Optional<Admin> opt = adminDAO.findById(adminId);
        if (opt.isEmpty()) {
            throw new InvalidCredentialsException("Admin account not found.");
        }

        Admin admin = opt.get();
        if (!PasswordUtil.verifyPassword(oldPassword, admin.getSalt(), admin.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password does not match.");
        }

        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);
        boolean updated = adminDAO.updatePassword(adminId, newHash, newSalt);
        if (updated && currentAdmin != null && currentAdmin.getAdminId() == adminId) {
            currentAdmin.setPasswordHash(newHash);
            currentAdmin.setSalt(newSalt);
        }
        return updated;
    }
}
