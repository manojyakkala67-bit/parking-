package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Model class representing a Payment transaction for a parking record.
 */
public class Payment {
    private int paymentId;
    private int recordId;
    private double amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentTime;
    private String transactionReference;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public Payment() {
        this.paymentMethod = PaymentMethod.CASH;
        this.paymentTime = LocalDateTime.now();
        this.status = PaymentStatus.SUCCESS;
        this.createdAt = LocalDateTime.now();
    }

    public Payment(int recordId, double amount, PaymentMethod paymentMethod, String transactionReference) {
        this.recordId = recordId;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.CASH;
        this.paymentTime = LocalDateTime.now();
        this.transactionReference = transactionReference;
        this.status = PaymentStatus.SUCCESS;
        this.createdAt = LocalDateTime.now();
    }

    public Payment(int paymentId, int recordId, double amount, PaymentMethod paymentMethod,
                   LocalDateTime paymentTime, String transactionReference, 
                   PaymentStatus status, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.recordId = recordId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.transactionReference = transactionReference;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
