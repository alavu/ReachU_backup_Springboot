package com.ReachU.ServiceBookingSystem.services.company;


import com.ReachU.ServiceBookingSystem.dto.AdDTO;
import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;
import com.ReachU.ServiceBookingSystem.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

public interface AdminService {

//    Long getUserIdFromUserDetails(UserDetails userDetails);

    List<User> getAllUsers();

    boolean postService(Long userId, AdDTO adDTO) throws IOException;

    List<AdDTO> getAllAds(Long userId);

    AdDTO getAdById(Long adId);

    boolean updateAd(Long adId, AdDTO adDTO) throws IOException;

    boolean deleteAd(Long adId);

    List<ReservationDTO> getAllAdBookings(Long adminId);

    boolean changeBookingStatus(Long bookingId, String status);

    Long getUserIdFromUserDetails(UserDetails userDetails);

    Long getAdminIdFromUserDetails(UserDetails userDetails);

//    boolean postService(UserDetails userDetails, AdDTO adDTO) throws IOException;
}
