package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.entity.Admin;
import com.ReachU.ServiceBookingSystem.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findAdminEntityByUserRole(UserRole userRole);
    Optional<Admin> findFirstByEmail(String email);

//    Optional<AdminEntity> findFirstById(Long Id);
}
