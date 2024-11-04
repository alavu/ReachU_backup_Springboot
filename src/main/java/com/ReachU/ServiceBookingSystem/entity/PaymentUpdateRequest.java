package com.ReachU.ServiceBookingSystem.entity;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
    private String paymentMode;
    private String paymentStatus;
}