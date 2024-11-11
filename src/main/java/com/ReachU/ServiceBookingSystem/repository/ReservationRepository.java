package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.entity.Reservation;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByAdminId(Long adminId);

    List<Reservation> findByPartnerIdAndReservationStatus(Long partnerId, ReservationStatus reservationStatus);

    List<Reservation> findAllByUserId(Long userId);

    @Query("select count (r) from Reservation r")
    long countTotalOrders();

    @Query("select sum(r.ad.price) from Reservation r where r.paymentStatus = 'Paid'")
    double calculateTotalRevenue();

    // Count total bookings by partner
    long countByPartnerId(Long partnerId);

    // Count bookings by partner and status
    long countByPartnerIdAndReservationStatus(Long partnerId, ReservationStatus reservationStatus);

    @Query("SELECT r FROM Reservation r WHERE r.bookDate >= :startDate AND r.bookDate <= :endDate")
    List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Reservation r WHERE r.bookDate = :bookingDate")
    List<Reservation> findByBookingDate(@Param("bookingDate") LocalDate bookDate);


}
