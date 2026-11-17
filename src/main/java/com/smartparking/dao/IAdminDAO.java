package com.smartparking.dao;

import com.smartparking.model.Admin;
import java.util.Optional;

/**
 * Data Access Object Interface for Admin accounts.
 */
public interface IAdminDAO {
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findById(int adminId);
    boolean updatePassword(int adminId, String newPasswordHash, String newSalt);
    boolean updateLastLogin(int adminId);
    boolean insert(Admin admin);
}
