package com.smartparking.dao;

import com.smartparking.dao.storage.DataStore;
import com.smartparking.model.Admin;

import java.util.Optional;

/**
 * Data Access Object for Admin accounts (Standalone In-Memory & Local Storage).
 */
public class AdminDAOImpl implements IAdminDAO {

    private final DataStore store = DataStore.getInstance();

    @Override
    public Optional<Admin> findByUsername(String username) {
        return store.findAdminByUsername(username);
    }

    @Override
    public Optional<Admin> findById(int adminId) {
        return store.findAdminById(adminId);
    }

    @Override
    public boolean updatePassword(int adminId, String newPasswordHash, String newSalt) {
        return store.updateAdminPassword(adminId, newPasswordHash, newSalt);
    }

    @Override
    public boolean updateLastLogin(int adminId) {
        return store.updateAdminLastLogin(adminId);
    }

    @Override
    public boolean insert(Admin admin) {
        return true;
    }
}
