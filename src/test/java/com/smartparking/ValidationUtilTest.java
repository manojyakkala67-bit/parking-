package com.smartparking;

import com.smartparking.util.ValidationException;
import com.smartparking.util.ValidationUtil;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Unit tests verifying user input validation rules.
 */
public class ValidationUtilTest {

    @Test
    public void testValidRegistrationNumbers() {
        ValidationUtil.validateRegistrationNumber("KA-01-AB-1234");
        ValidationUtil.validateRegistrationNumber("DL03XY9876");
        ValidationUtil.validateRegistrationNumber("MH 12 CD 5544");
        ValidationUtil.validateRegistrationNumber("HR-26-4321");
    }

    @Test(expected = ValidationException.class)
    public void testEmptyRegistrationNumberThrowsException() {
        ValidationUtil.validateRegistrationNumber("");
    }

    @Test(expected = ValidationException.class)
    public void testTooShortRegistrationNumberThrowsException() {
        ValidationUtil.validateRegistrationNumber("A1");
    }

    @Test
    public void testValidPhoneNumbers() {
        ValidationUtil.validatePhoneNumber("9876543210");
        ValidationUtil.validatePhoneNumber("+919876543210");
        ValidationUtil.validatePhoneNumber("98123 45678");
        ValidationUtil.validatePhoneNumber("9812-345-678");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidPhoneThrowsException() {
        ValidationUtil.validatePhoneNumber("12345"); // too short
    }

    @Test
    public void testValidOwnerNames() {
        ValidationUtil.validateOwnerName("Rahul Sharma");
        ValidationUtil.validateOwnerName("Dr. John Watson-Smith");
        ValidationUtil.validateOwnerName("A. P. J. Kalam");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidOwnerNameThrowsException() {
        ValidationUtil.validateOwnerName("R@hul #123");
    }

    @Test
    public void testValidSlotNumbers() {
        ValidationUtil.validateSlotNumber("A-101");
        ValidationUtil.validateSlotNumber("B-205");
        ValidationUtil.validateSlotNumber("BAY12");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidSlotNumberThrowsException() {
        ValidationUtil.validateSlotNumber("");
    }
}
