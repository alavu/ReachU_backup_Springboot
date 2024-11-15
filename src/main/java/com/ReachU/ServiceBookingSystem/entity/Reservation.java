package com.ReachU.ServiceBookingSystem.entity;

import com.ReachU.ServiceBookingSystem.dto.AdDTO;
import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_status")
//    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    private ReviewStatus reviewStatus;

    private LocalDate bookDate;

    private String paymentMode;

    private String paymentStatus;

    private Double price;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User admin;

    // New Partner Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ad ad;

    public ReservationDTO getReservationDto(){
        ReservationDTO dto = new ReservationDTO();
        dto.setId(id);
        dto.setServiceName(ad.getServiceName());
        dto.setBookDate(bookDate);
        dto.setReservationStatus(reservationStatus);
        dto.setReviewStatus(reviewStatus);
        dto.setAdId(ad.getId());
        dto.setAdminId(admin.getId());
        dto.setUserName(user.getName());
        dto.setPaymentMode(paymentMode);
        dto.setPaymentStatus(paymentStatus);
        dto.setPartnerId(partner.getId());
        dto.setPrice(ad.getPrice());
        System.out.println("getting the price:"+ ad.getPrice());
        return dto;
    }
}
