package com.ReachU.ServiceBookingSystem.services.client.clientService;


import com.ReachU.ServiceBookingSystem.dto.AdDTO;
import com.ReachU.ServiceBookingSystem.dto.AdDetailsForClientDTO;
import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;
import com.ReachU.ServiceBookingSystem.dto.ReviewDTO;
import com.ReachU.ServiceBookingSystem.entity.PaymentUpdateRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClientService {

    List<AdDTO> getAllAds();

    List<AdDTO> searchAdByName(String name);

//    boolean bookService(ReservationDTO reservationDTO);

    AdDetailsForClientDTO getAdDetailsByAdId(Long adId);

    List<ReservationDTO> getAllBookingsByUserId(Long userId);

    Boolean giveReview(ReviewDTO reviewDTO);

    Map<String, Long> bookService(ReservationDTO reservationDTO);

    Map<String, String> updateReservationPayment(Long id, PaymentUpdateRequest request);

    Long getUserIdFromUserDetails(UserDetails userDetails);

    List<ReservationDTO> getAllBookings(String status, LocalDate startDate, LocalDate endDate);

    List<ReservationDTO> getAllBookingsByCustomers();

    // Handling admin dashboard
    long getTotalOrders();

    long getTotalCustomers();

    double getTotalRevenue();

    long getTotalBookings();

    List<String> getWeeklyLabels();

    List<Double> getWeeklyRevenue();

    List<String> getMonthlyLabels();

    List<Double> getMonthlyRevenue();

    List<String> getYearlyLabels();

    double getYearlyRevenue();

    double getCustomRangeRevenue(LocalDate startDate, LocalDate endDate);
}
