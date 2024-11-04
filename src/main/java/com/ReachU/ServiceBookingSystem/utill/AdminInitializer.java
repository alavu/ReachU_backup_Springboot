package com.ReachU.ServiceBookingSystem.utill;

import com.ReachU.ServiceBookingSystem.entity.Admin;
import com.ReachU.ServiceBookingSystem.enums.UserRole;
import com.ReachU.ServiceBookingSystem.repository.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    @Transactional
    public void createAnAdminAccount() {
        Optional<Admin> optionalAdmin = adminRepository.findFirstByEmail(adminEmail);
        if (optionalAdmin.isEmpty()) {
            String password = passwordEncoder.encode(adminPassword);
            Admin admin = new Admin(1L,adminEmail,password, UserRole.ADMIN);
            adminRepository.save(admin);
            log.info("Admin account created successfully");
        } else {
            log.info("Admin account already exists");
        }
    }

}
