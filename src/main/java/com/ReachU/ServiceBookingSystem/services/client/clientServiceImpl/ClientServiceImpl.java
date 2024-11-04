package com.ReachU.ServiceBookingSystem.services.client.clientServiceImpl;

import com.ReachU.ServiceBookingSystem.dto.AdDTO;
import com.ReachU.ServiceBookingSystem.dto.AdDetailsForClientDTO;
import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;
import com.ReachU.ServiceBookingSystem.dto.ReviewDTO;
import com.ReachU.ServiceBookingSystem.entity.*;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.enums.ReviewStatus;
import com.ReachU.ServiceBookingSystem.exceptions.ResourceNotFoundException;
import com.ReachU.ServiceBookingSystem.repository.*;
import com.ReachU.ServiceBookingSystem.services.client.clientService.ClientService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    public List<AdDTO> getAllAds() {
        return adRepository.findAll().stream().map(Ad::getAdDto).collect(Collectors.toList());
    }

    @Override
    public List<AdDTO> searchAdByName(String name) {
        return adRepository.findAllByServiceNameContaining(name).stream().map(Ad::getAdDto).collect(Collectors.toList());
    }


    @Override
    public Map<String, Long> bookService(ReservationDTO reservationDTO) {
        Optional<Ad> optionalAd = adRepository.findById(reservationDTO.getAdId());
        Optional<User> optionalUser = userRepository.findById(reservationDTO.getUserId());
        Optional<Partner> optionalPartner = partnerRepository.findById(reservationDTO.getPartnerId());

        if (optionalAd.isPresent() && optionalUser.isPresent()) {
            Reservation reservation = new Reservation();
            reservation.setBookDate(reservationDTO.getBookDate());
            reservation.setReservationStatus(ReservationStatus.PENDING);
            reservation.setUser(optionalUser.get());
            reservation.setAd(optionalAd.get());
            reservation.setAdmin(optionalAd.get().getUser());
            reservation.setReviewStatus(ReviewStatus.FALSE);
            reservation.setPartner(optionalPartner.get());
            reservation.setPrice(optionalAd.get().getPrice());
            reservationRepository.save(reservation);

            // Return reservationId
            return Map.of("reservationId", reservation.getId());
        }
        throw new ResourceNotFoundException("Ad or User not found");
    }


    @Override
    public AdDetailsForClientDTO getAdDetailsByAdId(Long adId){
        Optional<Ad> optionalAd = adRepository.findById(adId);
        AdDetailsForClientDTO adDetailsForClientDTO = new AdDetailsForClientDTO();
        if(optionalAd.isPresent()){
            adDetailsForClientDTO.setAdDTO(optionalAd.get().getAdDto());

            List<Review> reviewList = reviewRepository.findAllByAdId(adId);
            adDetailsForClientDTO.setReviewDTOList(reviewList.stream().map(Review::getDto).collect(Collectors.toList()));
        }
        return adDetailsForClientDTO;
    }

    @Override
    public List<ReservationDTO> getAllBookingsByUserId(Long userId){
        return reservationRepository.findAllByUserId(userId).stream().map(Reservation::getReservationDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> updateReservationPayment(Long id, PaymentUpdateRequest request) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setPaymentMode(request.getPaymentMode());
            reservation.setPaymentStatus(request.getPaymentStatus());
            reservationRepository.save(reservation);

            return Map.of("message", "Payment updated successfully");
        } else {
            throw new ResourceNotFoundException("Reservation not found with id: " + id);
        }
    }

    @Override
    public Boolean giveReview(ReviewDTO reviewDTO){
        Optional<User> optionalUser = userRepository.findById(reviewDTO.getUserId());
        Optional<Reservation> optionalBooking = reservationRepository.findById(reviewDTO.getBookId());

        if(optionalUser.isPresent() && optionalBooking.isPresent()){
            Review review = new Review();

            review.setReviewDate(new Date());
            review.setReview(reviewDTO.getReview());
            review.setRating(reviewDTO.getRating());

            review.setUser(optionalUser.get());
            review.setAd(optionalBooking.get().getAd());

            reviewRepository.save(review);

            Reservation booking = optionalBooking.get();
            booking.setReviewStatus(ReviewStatus.TRUE);

            reservationRepository.save(booking);

            return true;
        }
        return false;
    }

    @Override
    public Long getUserIdFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        System.out.println(username + " username");
        Optional<User> userEntity = userRepository.findFirstByEmail(username);
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return userEntity.get().getId();
    }

    @Override
    public List<ReservationDTO> getAllBookings(String status, LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findAll();

        // Filter by status if provided
        if (status != null) {
            reservations = reservations.stream()
                    .filter(res -> res.getReservationStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        // Filter by date range if provided
        if (startDate != null && endDate != null) {
            reservations = reservations.stream()
                    .filter(res -> {
                        LocalDate bookingDate = res.getBookDate();
                        return !bookingDate.isBefore(startDate) && !bookingDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        }

        // Convert to DTO
        return reservations.stream().map(Reservation::getReservationDto).collect(Collectors.toList());
    }

//    private ReservationDTO convertToDTO(Reservation reservation) {
//        return new ReservationDTO(
//                reservation.getId(),
//                reservation.getUser().getId(),
//                reservation.getUser().getName(),
//                reservation.getAd().getServiceName(),
//                reservation.getBookDate(),
//                reservation.getReservationStatus().toString()
//        );
//    }

    @Override
    public List<ReservationDTO> getAllBookingsByCustomers() {
        return reservationRepository.findAll().stream().map(Reservation::getReservationDto).collect(Collectors.toList());
    }

    @Override
    public long getTotalOrders() {
        return reservationRepository.count();
    }

    @Override
    public long getTotalCustomers() {
        return userRepository.count();
    }

    @Override
    public double getTotalRevenue() {
        return reservationRepository.findAll().stream()
                .mapToDouble(reservation -> reservation.getAd().getPrice())
                .sum();
    }

    @Override
    public long getTotalBookings() {
        return reservationRepository.findAll().stream()
                .filter(reservation -> "Paid".equals(reservation.getPaymentStatus()))
                .count();
    }
}

