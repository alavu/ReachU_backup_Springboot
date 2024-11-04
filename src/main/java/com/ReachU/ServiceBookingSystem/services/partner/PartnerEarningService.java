package com.ReachU.ServiceBookingSystem.services.partner;

import com.ReachU.ServiceBookingSystem.entity.Partner;

import java.math.BigDecimal;


public interface PartnerEarningService {
    BigDecimal getTotalEarnings(Long partnerId);
    long getTotalBookings(Long partnerId);
    long getCurrentBookings(Long partnerId);
    long getTotalCanceled(Long partnerId);
    void updatePartnerEarnings(Partner partner, String jobStatus, BigDecimal earnings);
    BigDecimal calculateEarnings(Partner partner);
}
