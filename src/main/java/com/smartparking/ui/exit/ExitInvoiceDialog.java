package com.smartparking.ui.exit;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.Payment;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.ExportUtil;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

/**
 * Printable Exit Tax Invoice & Settlement Receipt Dialog.
 */
public class ExitInvoiceDialog extends JDialog {

    private final ParkingRecord record;
    private final Payment payment;
    private final JTextArea txtReceipt = new JTextArea();

    public ExitInvoiceDialog(JFrame parent, ParkingRecord record, Payment payment) {
        super(parent, "Parking Tax Invoice - #" + record.getRecordId(), true);
        this.record = record;
        this.payment = payment;
        initUI();
    }

    private void initUI() {
        setSize(520, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("✓ Payment Settled & Slot Released");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Invoice INV-" + String.format("%06d", record.getRecordId()) + " | " + record.getVehicleNumber());
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Text Receipt Area
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtReceipt.setEditable(false);
        txtReceipt.setFont(UITheme.FONT_MONO);
        txtReceipt.setBackground(new Color(250, 250, 250));
        txtReceipt.setText(ExportUtil.generateExitInvoice(record, payment));
        txtReceipt.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(UITheme.CONTENT_BG);

        ModernButton btnCopy = new ModernButton("Copy Invoice Text", ModernButton.Style.OUTLINE);
        ModernButton btnPrint = new ModernButton("Print Invoice", ModernButton.Style.PRIMARY);
        ModernButton btnClose = new ModernButton("Close", ModernButton.Style.SECONDARY);

        btnCopy.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtReceipt.getText()), null);
            DialogUtil.showSuccess(this, "Invoice text copied to clipboard!", "Copied");
        });

        btnPrint.addActionListener(e -> {
            try {
                boolean ok = txtReceipt.print();
                if (ok) {
                    DialogUtil.showSuccess(this, "Sent to printer successfully.", "Printed");
                }
            } catch (Exception ex) {
                DialogUtil.showError(this, "Printing error: " + ex.getMessage(), "Error");
            }
        });

        btnClose.addActionListener(e -> dispose());

        footer.add(btnCopy);
        footer.add(btnPrint);
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }
}
