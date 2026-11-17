package com.smartparking.util;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.Payment;
import com.smartparking.model.RevenueReport;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * Utility for exporting reports, generating text receipts, and dumping tables to CSV.
 */
public final class ExportUtil {

    private ExportUtil() {
    }

    /**
     * Exports a Swing JTable to a CSV file.
     */
    public static void exportTableToCSV(JTable table, File file) throws IOException {
        TableModel model = table.getModel();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // Write Headers
            for (int col = 0; col < model.getColumnCount(); col++) {
                pw.print("\"" + escapeCsv(model.getColumnName(col)) + "\"");
                if (col < model.getColumnCount() - 1) pw.print(",");
            }
            pw.println();

            // Write Rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object val = model.getValueAt(row, col);
                    pw.print("\"" + escapeCsv(val != null ? val.toString() : "") + "\"");
                    if (col < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }
        }
    }

    private static String escapeCsv(String text) {
        if (text == null) return "";
        return text.replace("\"", "\"\"");
    }

    /**
     * Generates a printable ASCII / text Entry Pass for a parked vehicle.
     */
    public static String generateEntryPass(ParkingRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          SMART PARKING MANAGEMENT SYSTEM           \n");
        sb.append("                    ENTRY PASS                      \n");
        sb.append("====================================================\n");
        sb.append(String.format(" PASS ID:        #SMP-ENT-%05d\n", record.getRecordId()));
        sb.append(String.format(" DATE & TIME:    %s\n", DateTimeUtil.formatDateTime(record.getEntryTime())));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" VEHICLE NUMBER: %s\n", record.getVehicleNumber()));
        sb.append(String.format(" VEHICLE TYPE:   %s\n", record.getVehicleType().getDisplayName()));
        sb.append(String.format(" OWNER NAME:     %s\n", record.getOwnerName()));
        sb.append(String.format(" PHONE NUMBER:   %s\n", record.getPhoneNumber()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" ASSIGNED SLOT:  [ %s ]\n", record.getSlotNumber()));
        sb.append(String.format(" HOURLY RATE:    ₹%.2f / hour\n", record.getVehicleType().getHourlyRate()));
        sb.append("----------------------------------------------------\n");
        sb.append(" * Minimum 1 hour parking charge applies.\n");
        sb.append(" * Please retain this pass until exit.\n");
        sb.append(" * Loss of ticket will incur verification penalty.\n");
        sb.append("====================================================\n");
        sb.append("             THANK YOU FOR PARKING WITH US!         \n");
        sb.append("====================================================\n");
        return sb.toString();
    }

    /**
     * Generates a printable ASCII / text Exit Tax Invoice & Receipt.
     */
    public static String generateExitInvoice(ParkingRecord record, Payment payment) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          SMART PARKING MANAGEMENT SYSTEM           \n");
        sb.append("                 TAX INVOICE & RECEIPT              \n");
        sb.append("====================================================\n");
        sb.append(String.format(" INVOICE NO:     INV-%06d\n", record.getRecordId()));
        sb.append(String.format(" TRANSACTION REF:%s\n", payment != null ? payment.getTransactionReference() : "N/A"));
        sb.append(String.format(" DATE:           %s\n", DateTimeUtil.formatDateTime(LocalDateTime.now())));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" VEHICLE NUMBER: %s\n", record.getVehicleNumber()));
        sb.append(String.format(" VEHICLE TYPE:   %s\n", record.getVehicleType().getDisplayName()));
        sb.append(String.format(" OWNER NAME:     %s\n", record.getOwnerName()));
        sb.append(String.format(" PARKING SLOT:   %s\n", record.getSlotNumber()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" ENTRY TIME:     %s\n", DateTimeUtil.formatDateTime(record.getEntryTime())));
        sb.append(String.format(" EXIT TIME:      %s\n", DateTimeUtil.formatDateTime(record.getExitTime())));
        sb.append(String.format(" DURATION:       %s\n", record.getFormattedDuration()));
        sb.append(String.format(" BILLED HOURS:   %d Hour(s)\n", record.getDurationHours() != null ? record.getDurationHours() : 1));
        sb.append(String.format(" HOURLY TARIFF:  ₹%.2f\n", record.getVehicleType().getHourlyRate()));
        sb.append("----------------------------------------------------\n");
        double amount = payment != null ? payment.getAmount() : (record.getTotalFee() != null ? record.getTotalFee() : 0.0);
        sb.append(String.format(" TOTAL AMOUNT:   ₹%.2f\n", amount));
        sb.append(String.format(" PAYMENT MODE:   %s\n", payment != null ? payment.getPaymentMethod().getDisplayName() : "CASH"));
        sb.append(String.format(" PAYMENT STATUS: %s\n", payment != null ? payment.getStatus().getDisplayName() : "SUCCESS"));
        sb.append("====================================================\n");
        sb.append("              HAVE A SAFE JOURNEY!                  \n");
        sb.append("====================================================\n");
        return sb.toString();
    }
}
