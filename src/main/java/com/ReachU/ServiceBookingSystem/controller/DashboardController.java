package com.ReachU.ServiceBookingSystem.controller;

import com.ReachU.ServiceBookingSystem.services.client.clientService.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

}
