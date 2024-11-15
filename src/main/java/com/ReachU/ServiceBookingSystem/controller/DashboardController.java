package com.ReachU.ServiceBookingSystem.controller;

import com.ReachU.ServiceBookingSystem.services.client.clientService.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/dashboard")
public class DashboardController {

    private final ClientService clientService;

    @GetMapping("/total-orders")
    public ResponseEntity<Map<String, Long>> getTotalOrders() {
        long totalOrders = clientService.getTotalOrders();
        return ResponseEntity.ok(Map.of("totalOrders", totalOrders));
    }

    @GetMapping("/total-customers")
    public ResponseEntity<Map<String, Long>> getTotalCustomers() {
        long totalCusomers = clientService.getTotalCustomers();
        return  ResponseEntity.ok(Map.of("totalCustomers", totalCusomers));
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<Map<String,Double>> getTotalRevenue() {
        double totalRevenue = clientService.getTotalRevenue();
        return ResponseEntity.ok(Map.of("totalRevenue", totalRevenue));
    }

    @GetMapping("/total-bookings")
    public ResponseEntity<Map<String, Long>> getTotalBookings() {
        long totalBookings = clientService.getTotalBookings();
        System.out.println("Total Booking:"+totalBookings);
        return ResponseEntity.ok(Map.of("totalBookings", totalBookings));
    }

    @GetMapping("/weekly-revenue")
    public ResponseEntity<Map<String, Object>> getWeeklyRevenue() {
        Map<String, Object> response = new HashMap<>();
        response.put("labels", clientService.getWeeklyLabels());
        response.put("data", clientService.getWeeklyRevenue());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenue() {
        Map<String, Object> response = new HashMap<>();
        response.put("labels", clientService.getMonthlyLabels());
        response.put("data", clientService.getMonthlyRevenue());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly-revenue")
    public ResponseEntity<Map<String, Object>> getYearlyRevenue() {
        Map<String, Object> response = new HashMap<>();
        response.put("labels", clientService.getYearlyLabels());
        response.put("data", clientService.getYearlyRevenueList()); // Change to return a list
        return ResponseEntity.ok(response);
    }

    @GetMapping("/custom-range-revenue")
    public ResponseEntity<Map<String, Object>> getCustomRangeRevenue(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<String> labels = start.datesUntil(end.plusDays(1))
                .map(date -> date.format(DateTimeFormatter.ofPattern("E MMM d")))
                .collect(Collectors.toList());

        List<Double> data = start.datesUntil(end.plusDays(1))
                .map(clientService::calculateDailyRevenue) // Use clientService to access calculateDailyRevenue
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
