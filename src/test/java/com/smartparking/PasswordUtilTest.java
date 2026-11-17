package com.smartparking;

import com.smartparking.util.PasswordUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests verifying cryptographic password hashing, salts, and verification.
 */
public class PasswordUtilTest {

    @Test
    public void testSaltGeneration() {
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(16, salt1.length());
        assertEquals(16, salt2.length());
        assertNotEquals(salt1, salt2); // Two randomly generated salts must be distinct
    }

    @Test
    public void testPasswordHashingAndVerification() {
        String rawPassword = "SecureAdminPassword!2026";
        String salt = PasswordUtil.generateSalt();

        String hash = PasswordUtil.hashPassword(rawPassword, salt);
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 produces 64 hex characters (256 bits)

        // Same password and salt must produce exact same hash
        String hashAgain = PasswordUtil.hashPassword(rawPassword, salt);
        assertEquals(hash, hashAgain);

        // Verification must succeed
        assertTrue(PasswordUtil.verifyPassword(rawPassword, salt, hash));

        // Wrong password must fail
        assertFalse(PasswordUtil.verifyPassword("WrongPassword123", salt, hash));

        // Wrong salt must fail
        String differentSalt = PasswordUtil.generateSalt();
        assertFalse(PasswordUtil.verifyPassword(rawPassword, differentSalt, hash));
    }

    @Test
    public void testDefaultAdminSeedVerification() {
        String seededSalt = "e3b0c44298fc1c14";
        String expectedHash = "cf8eaef3ff404d96b133e4f757749eef71a512255619068b1c27858f5ebe2026";

        assertTrue(PasswordUtil.verifyPassword("admin123", seededSalt, expectedHash));
        assertFalse(PasswordUtil.verifyPassword("admin", seededSalt, expectedHash));
    }
}
