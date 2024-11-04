package com.ReachU.ServiceBookingSystem.services.company;

import com.ReachU.ServiceBookingSystem.dto.AdDTO;
import com.ReachU.ServiceBookingSystem.dto.ReservationDTO;

import com.ReachU.ServiceBookingSystem.entity.*;
import com.ReachU.ServiceBookingSystem.enums.ReservationStatus;
import com.ReachU.ServiceBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final AdminRepository adminRepository;
    private final ReservationRepository reservationRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("Number of users fetched: " + users.size());
        return users;
    }

    @Override
    public boolean postService(Long userId, AdDTO adDTO) throws IOException {
        System.out.println("Received AdDTO: " + adDTO);

        // Validate userId and adDTO fields
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (adDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        // Fetch the user
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }

        Ad ad = new Ad();
        ad.setServiceName(adDTO.getServiceName());
        ad.setDescription(adDTO.getDescription());
        ad.setImg(adDTO.getImg() != null ? adDTO.getImg().getBytes() : null); // Handle optional image
        ad.setPrice(adDTO.getPrice());
        ad.setUser(optionalUser.get());

        // Fetch and set category
        Optional<Category> categoryOptional = categoryRepository.findById(adDTO.getCategoryId());
        if (categoryOptional.isPresent()) {
            ad.setCategory(categoryOptional.get());
        } else {
            throw new IllegalArgumentException("Invalid Category ID: " + adDTO.getCategoryId());
        }

        // Fetch and set subcategory, if provided
        if (adDTO.getSubcategoryId() != null) {
            Optional<Subcategory> subCategoryOptional = subcategoryRepository.findById(adDTO.getSubcategoryId());
            if (subCategoryOptional.isPresent()) {
                ad.setSubcategory(subCategoryOptional.get());
            } else {
                throw new RuntimeException("Invalid Subcategory ID: " + adDTO.getSubcategoryId());
            }
        } else {
            ad.setSubcategory(null); // Explicitly set to null if subcategoryId is not provided
        }

        adRepository.save(ad);
        return true;
    }


    public List<AdDTO> getAllAds(Long userId) {
        return adRepository.findAllByUserId(userId).stream().map(Ad::getAdDto).collect(Collectors.toList());
    }

    public AdDTO getAdById(Long adId) {
        Optional<Ad> optionalAd = adRepository.findById(adId);
        return optionalAd.map(Ad::getAdDto).orElse(null);
    }

    public boolean updateAd(Long adId, AdDTO adDTO) throws IOException {
        Optional<Ad> optionalAd = adRepository.findById(adId);
        if (optionalAd.isPresent()) {
            Ad ad = optionalAd.get();

            ad.setServiceName(adDTO.getServiceName());
            ad.setDescription(adDTO.getDescription());
            ad.setPrice(adDTO.getPrice());

            // Fetch and set category
            Optional<Category> categoryOptional = categoryRepository.findById(adDTO.getCategoryId());
            if (categoryOptional.isPresent()) {
                ad.setCategory(categoryOptional.get());
            } else {
                throw new IllegalArgumentException("Invalid Category ID:" + adDTO.getCategoryId());
            }

            // Fetch and set subcategory, if provided
            if (adDTO.getSubcategoryId() != null) {
                Optional<Subcategory> subcategoryOptional = subcategoryRepository.findById(adDTO.getSubcategoryId());
                if (subcategoryOptional.isPresent()) {
                    ad.setSubcategory(subcategoryOptional.get());
                } else {
                    throw new RuntimeException("Invalid Subcategory ID: " + adDTO.getSubcategoryId());
                }
            } else {
                ad.setSubcategory(null); // Explicitly set to null if subcategoryId is not provided
            }

            if (adDTO.getImg() != null) {
                ad.setImg(adDTO.getImg().getBytes());
            }

            adRepository.save(ad);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteAd(Long adId) {
        Optional<Ad> optionalAd = adRepository.findById(adId);
        if (optionalAd.isPresent()) {
            adRepository.delete(optionalAd.get());
            return true;
        }
        return false;
    }

    public List<ReservationDTO> getAllAdBookings(Long adminId) {
        return reservationRepository.findAllByAdminId(adminId)
                .stream().map(Reservation::getReservationDto).collect(Collectors.toList());
    }

    public boolean changeBookingStatus(Long bookingId, String status) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(bookingId);
        if (optionalReservation.isPresent()) {
            Reservation existingReservation = optionalReservation.get();
            if (Objects.equals(status, "Approve")) {
                existingReservation.setReservationStatus(ReservationStatus.APPROVED);
            } else {
                existingReservation.setReservationStatus(ReservationStatus.REJECTED);
            }

            reservationRepository.save(existingReservation);
            return true;
        }
        return false;
    }

    @Override
    public Long getUserIdFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        System.out.println(username + " username");
        Optional<User> user = userRepository.findUserByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get().getId();
    }

    @Override
    public Long getAdminIdFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        System.out.println(username + " username");
        Optional<Admin> adminEntity = adminRepository.findFirstByEmail(username);
        if (adminEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return adminEntity.get().getId();
    }

}
