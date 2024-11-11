package com.ReachU.ServiceBookingSystem.services.partner;

import com.ReachU.ServiceBookingSystem.entity.Partner;
import com.ReachU.ServiceBookingSystem.entity.PartnerEarnings;
import com.ReachU.ServiceBookingSystem.entity.Reservation;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.repository.PartnerEarningsRepository;
import com.ReachU.ServiceBookingSystem.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@AllArgsConstructor
public class PartnerEarningServiceImpl implements PartnerEarningService {

    private final PartnerEarningsRepository partnerEarningsRepository;

    private final ReservationRepository reservationRepository;

    @Override
    public BigDecimal getTotalEarnings(Long partnerId) {
        List<PartnerEarnings> earningsList = partnerEarningsRepository.findByPartnerId(partnerId);
        BigDecimal totalEarnings = earningsList.stream()
                .map(PartnerEarnings::getEarnings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total Earnings for Partner " + partnerId + ": " + totalEarnings);
        return totalEarnings;
    }

    // Update the partner earning
    @Override
    public void updatePartnerEarnings(Partner partner, String jobStatus, BigDecimal earnings) {
        PartnerEarnings partnerEarnings = partnerEarningsRepository.findByPartnerIdAndBookingId(partner.getId(), partner.getId())
                .orElse(new PartnerEarnings());  // If no record exists, create a new one

        partnerEarnings.setPartnerId(partner.getId());
        partnerEarnings.setBookingId(partner.getId());
        partnerEarnings.setReservationStatus(ReservationStatus.valueOf(jobStatus));


        if ("COMPLETED".equalsIgnoreCase(jobStatus)) {
            partnerEarnings.setEarnings(earnings);
            partnerEarnings.setCompletedDate(LocalDateTime.now());
        } else if ("CANCELLED".equalsIgnoreCase(jobStatus)) {
            partnerEarnings.setEarnings(BigDecimal.ZERO);  // No earnings for canceled jobs
            partnerEarnings.setCompletedDate(null);
        }

        partnerEarningsRepository.save(partnerEarnings);  // Save the updated earnings record
        // Update the Reservation table with the reservation status
        Reservation reservation = reservationRepository.findById(partner.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found for partner ID: " + partner.getId()));

        reservation.setReservationStatus(ReservationStatus.valueOf(jobStatus));
        reservationRepository.save(reservation);
    }

    @Override
    // Calculating the total earning
    public BigDecimal calculateEarnings(Partner partner) {
        // Ensure partner has bookings before calculating earnings
        List<Reservation> bookings = partner.getBookings();
        if (bookings == null || bookings.isEmpty()) {
            // If no bookings, return 0 earnings
            return BigDecimal.ZERO;
        }

        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal earningsPercentage = new BigDecimal("0.8");  // 80% of the booking price

        // Iterate over each booking to calculate total earnings
        for (Reservation booking : bookings) {
            if (booking.getPrice() != null) {
                BigDecimal bookingPrice = BigDecimal.valueOf(booking.getPrice());
                BigDecimal earnings = bookingPrice.multiply(earningsPercentage);

                // Add to total earnings
                totalEarnings = totalEarnings.add(earnings);
            }
        }

        // Apply additional logic for bonuses or penalties (optional)
        long totalBookings = getTotalBookings(partner.getId());
        if (totalBookings > 100) {
            BigDecimal bonusPercentage = new BigDecimal("0.05");  // 5% bonus
            BigDecimal bonus = totalEarnings.multiply(bonusPercentage);
            totalEarnings = totalEarnings.add(bonus);
        }

        long totalCancellations = getTotalCanceled(partner.getId());
        if (totalCancellations > 10) {
            BigDecimal penaltyPercentage = new BigDecimal("0.10");  // 10% penalty
            BigDecimal penalty = totalEarnings.multiply(penaltyPercentage);
            totalEarnings = totalEarnings.subtract(penalty);
        }

        return totalEarnings;  // Final earnings after applying bonuses and penalties
    }


    @Override
    // Get total bookings for the partner
    public long getTotalBookings(Long partnerId) {
        return reservationRepository.countByPartnerId(partnerId);
    }

    @Override
    // Get current (active) bookings for the partner
    public long getCurrentBookings(Long partnerId) {
        return reservationRepository.countByPartnerIdAndReservationStatus(partnerId, ReservationStatus.APPROVED);
    }

    // Get total canceled bookings for the partner
    public long getTotalCanceled(Long partnerId) {
        return reservationRepository.countByPartnerIdAndReservationStatus(partnerId, ReservationStatus.CANCELLED);
    }

    @Override
    public Map<String, Object> getWeeklyRevenueBreakdown(Long partnerId) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        List<String> labels = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        List<BigDecimal> dailyRevenues = new ArrayList<>(Collections.nCopies(7, BigDecimal.ZERO));

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            LocalDateTime startOfDay = day.atStartOfDay();
            LocalDateTime endOfDay = day.atTime(23, 59, 59);

            BigDecimal dailyRevenue = partnerEarningsRepository.findByPartnerIdAndCompletedDateBetween(partnerId, startOfDay, endOfDay)
                    .stream()
                    .map(PartnerEarnings::getEarnings)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dailyRevenues.set(i, dailyRevenue);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", dailyRevenues);

        return response;
    }

    @Override
    public Map<String, Object> getMonthlyRevenueBreakdown(Long partnerId) {
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        List<String> labels = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");
        List<BigDecimal> weeklyRevenues = new ArrayList<>(Collections.nCopies(4, BigDecimal.ZERO));

        for (int i = 0; i < 4; i++) {
            LocalDate startOfWeek = startOfMonth.plusDays(i * 7);
            LocalDate endOfWeek = startOfWeek.plusDays(6).isAfter(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()))
                    ? LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
                    : startOfWeek.plusDays(6);

            LocalDateTime startOfWeekTime = startOfWeek.atStartOfDay();
            LocalDateTime endOfWeekTime = endOfWeek.atTime(23, 59, 59);

            BigDecimal weeklyRevenue = partnerEarningsRepository.findByPartnerIdAndCompletedDateBetween(partnerId, startOfWeekTime, endOfWeekTime)
                    .stream()
                    .map(PartnerEarnings::getEarnings)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            weeklyRevenues.set(i, weeklyRevenue);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", weeklyRevenues);

        return response;
    }

    @Override
    public Map<String, Object> getYearlyRevenueBreakdown(Long partnerId) {
        LocalDate startOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        List<String> labels = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        List<BigDecimal> monthlyRevenues = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));

        for (int i = 0; i < 12; i++) {
            LocalDate startOfMonth = startOfYear.plusMonths(i);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime startOfMonthTime = startOfMonth.atStartOfDay();
            LocalDateTime endOfMonthTime = endOfMonth.atTime(23, 59, 59);

            BigDecimal monthlyRevenue = partnerEarningsRepository.findByPartnerIdAndCompletedDateBetween(partnerId, startOfMonthTime, endOfMonthTime)
                    .stream()
                    .map(PartnerEarnings::getEarnings)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyRevenues.set(i, monthlyRevenue);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", monthlyRevenues);

        return response;
    }
}