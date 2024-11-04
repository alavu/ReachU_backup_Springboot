package com.ReachU.ServiceBookingSystem.controller;

import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;
import com.ReachU.ServiceBookingSystem.dto.ReviewDTO;
import com.ReachU.ServiceBookingSystem.entity.PaymentUpdateRequest;
import com.ReachU.ServiceBookingSystem.entity.Reservation;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.exceptions.ResourceNotFoundException;
import com.ReachU.ServiceBookingSystem.repository.ReservationRepository;
import com.ReachU.ServiceBookingSystem.services.client.clientService.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/ads")
    public ResponseEntity<?> getAllAds(){
        return ResponseEntity.ok(clientService.getAllAds());
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchAdByService(@PathVariable String name){
        return ResponseEntity.ok(clientService.searchAdByName(name));
    }

    @PostMapping("/book-service")
    public ResponseEntity<?> bookService(@RequestBody ReservationDTO reservationDTO) {
        try {
            System.out.println("Receiving Dto:"+ reservationDTO);
            Map<String, Long> response = clientService.bookService(reservationDTO);
            System.out.println("Response Dto:"+ response);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ad/{adId}")
    public ResponseEntity<?> getAdDetailsByAdId(@PathVariable Long adId){
        return ResponseEntity.ok(clientService.getAdDetailsByAdId(adId));
    }

    @GetMapping("/my-bookings/{userId}")
    public ResponseEntity<?> getAllBookingsByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(clientService.getAllBookingsByUserId(userId));
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<?> updateReservationPayment(@PathVariable Long id, @RequestBody PaymentUpdateRequest request) {
    try {
        Map<String, String> response = clientService.updateReservationPayment(id, request);
        return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

    @PostMapping("/review")
    public ResponseEntity<?> giveReview(@RequestBody ReviewDTO reviewDTO){
        Boolean success = clientService.giveReview(reviewDTO);
        if(success){
            return ResponseEntity.status(HttpStatus.OK).build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

@GetMapping("/bookings")
public ResponseEntity<?> getBookingsForPartner(
        @PathVariable Long partnerId,
        @RequestParam(required = false) String status, // "COMPLETED" or "UPCOMING"
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<ReservationDTO> bookings = clientService.getAllBookings(status, startDate, endDate);
    return ResponseEntity.ok(bookings);
}

@GetMapping("/customer/bookings")
    public ResponseEntity<List> getAllBookings() {
        List<ReservationDTO> bookings = clientService.getAllBookingsByCustomers();
        return ResponseEntity.ok(bookings);
    }
}

