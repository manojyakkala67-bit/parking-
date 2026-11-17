package com.smartparking.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility for formatting and parsing date and time representations.
 * Uses Java 8+ Date and Time API (java.time).
 */
public final class DateTimeUtil {

    public static final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
    public static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("hh:mm:ss a");
    public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    public static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private DateTimeUtil() {
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_DATETIME);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DISPLAY_DATE);
    }

    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_TIME);
    }

    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null) return "N/A";
        LocalDateTime endTime = (end != null) ? end : LocalDateTime.now();
        Duration duration = Duration.between(start, endTime);
        long totalMinutes = Math.max(0, duration.toMinutes());
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours == 0) {
            return minutes + " min" + (minutes == 1 ? "" : "s");
        }
        return String.format("%d hr%s %d min%s", 
                hours, (hours == 1 ? "" : "s"), 
                minutes, (minutes == 1 ? "" : "s"));
    }

    public static LocalDateTime parseIso(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(text.trim(), ISO_DATETIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(text.trim());
        }
    }
}
