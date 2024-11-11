package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.entity.PartnerEarnings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartnerEarningsRepository extends JpaRepository<PartnerEarnings, Long> {
    List<PartnerEarnings> findByPartnerId(Long partnerId);
    Optional<PartnerEarnings> findByPartnerIdAndBookingId(Long partnerId, Long bookingId);
    List<PartnerEarnings> findByPartnerIdAndCompletedDateBetween(Long partnerId, LocalDateTime start, LocalDateTime end);
}
