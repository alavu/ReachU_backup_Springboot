package com.ReachU.ServiceBookingSystem.controller;

import com.ReachU.ServiceBookingSystem.services.partner.PartnerEarningService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/partners")
@AllArgsConstructor
public class PartnerDashboardController {
    private final PartnerEarningService partnerEarningService;

    // API to get dashboard statistics
    @GetMapping("/{partnerId}/stats")
    public Map<String, Object> getPartnerDashboardStats(@PathVariable Long partnerId) {
        Map<String, Object> response = new HashMap<>();

        // Total revenue for the partner
        BigDecimal totalRevenue = partnerEarningService.getTotalEarnings(partnerId);

        // Total bookings for the partner
        long totalBookings = partnerEarningService.getTotalBookings(partnerId);

        // Current (active) bookings for the partner
        long currentBookings = partnerEarningService.getCurrentBookings(partnerId);

        // Total canceled bookings for the partner
        long totalCancellations = partnerEarningService.getTotalCanceled(partnerId);

        // Prepare the response data
        response.put("totalRevenue", totalRevenue);
        response.put("totalBookings", totalBookings);
        response.put("currentBookings", currentBookings);
        response.put("totalCancellations", totalCancellations);

        return response;
    }

    // API to get graph data (for weekly/monthly stats)
    @GetMapping("/{partnerId}/graph-data")
    public Map<String, Object> getGraphData(@PathVariable Long partnerId, @RequestParam String period) {
        // Logic for fetching and calculating graph data (weekly/monthly)
        // This part is left open for your specific logic based on your requirements

        Map<String, Object> graphData = new HashMap<>();
        // Add logic to fetch graph data based on the period (weekly/monthly)

        return graphData;
    }

    @GetMapping("/{partnerId}/weekly-revenue")
    public Map<String, Object> getWeeklyRevenue(@PathVariable Long partnerId) {
        return partnerEarningService.getWeeklyRevenueBreakdown(partnerId);
    }

    @GetMapping("/{partnerId}/monthly-revenue")
    public Map<String, Object> getMonthlyRevenue(@PathVariable Long partnerId) {
        return partnerEarningService.getMonthlyRevenueBreakdown(partnerId);
    }

    @GetMapping("/{partnerId}/yearly-revenue")
    public Map<String, Object> getYearlyRevenue(@PathVariable Long partnerId) {
        return partnerEarningService.getYearlyRevenueBreakdown(partnerId);
    }
}
