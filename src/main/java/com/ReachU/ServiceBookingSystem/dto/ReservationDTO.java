package com.ReachU.ServiceBookingSystem.dto;

import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.enums.ReviewStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ReservationDTO {

    private Long id;

    private LocalDate bookDate;

    private String serviceName;

    private ReservationStatus reservationStatus;

    private ReviewStatus reviewStatus;

    private Long userId;

    private Long partnerId;

    private String userName;

    private Long adminId;

    private Long adId;

    private String paymentMode;

    private String paymentStatus;

    private Double price;

//    private String status;


    public ReservationDTO(Long id, Long userId, String userName, String serviceName, LocalDate endDate,ReservationStatus reservationStatus) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.serviceName = serviceName;
        this.bookDate = bookDate;
        this.reservationStatus = reservationStatus;
    }

    public  ReservationDTO() {

    }
}
