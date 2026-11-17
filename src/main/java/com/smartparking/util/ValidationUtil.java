package com.smartparking.util;

import java.util.regex.Pattern;

/**
 * Utility for strict validation of user inputs across the application.
 */
public final class ValidationUtil {

    // Reg pattern matches standard plates like KA-01-AB-1234, DL 03 1234, or general alphanumeric 4-15 chars
    private static final Pattern REG_PATTERN = Pattern.compile("^[A-Z0-9]{2,4}[- ]?[A-Z0-9]{1,4}[- ]?[A-Z0-9]{1,4}[- ]?[0-9]{1,4}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern GENERAL_REG_PATTERN = Pattern.compile("^[A-Za-z0-9- ]{4,15}$");
    
    // 10 to 15 digits, optional leading +
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    // Standard slot pattern like A-101, B-205, S1, etc.
    private static final Pattern SLOT_PATTERN = Pattern.compile("^[A-Za-z0-9-]{2,10}$");

    // Name pattern: letters, spaces, dots, hyphens
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z. '-]{2,50}$");

    private ValidationUtil() {
    }

    public static void validateRegistrationNumber(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new ValidationException("Vehicle registration number is required.");
        }
        String clean = regNo.trim();
        if (!GENERAL_REG_PATTERN.matcher(clean).matches()) {
            throw new ValidationException("Invalid vehicle registration number format (e.g., KA-01-AB-1234).");
        }
    }

    public static void validatePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Mobile phone number is required.");
        }
        String clean = phone.trim().replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(clean).matches()) {
            throw new ValidationException("Phone number must contain between 10 and 15 valid digits.");
        }
    }

    public static void validateOwnerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Owner name is required.");
        }
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new ValidationException("Owner name contains invalid characters (letters only, min 2 characters).");
        }
    }

    public static void validateSlotNumber(String slotNo) {
        if (slotNo == null || slotNo.trim().isEmpty()) {
            throw new ValidationException("Slot number is required.");
        }
        if (!SLOT_PATTERN.matcher(slotNo.trim()).matches()) {
            throw new ValidationException("Invalid slot number format (e.g., A-101).");
        }
    }

    public static String sanitize(String input) {
        return input == null ? "" : input.trim();
    }
}
