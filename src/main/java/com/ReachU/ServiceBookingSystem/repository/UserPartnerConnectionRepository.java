package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.entity.UserPartnerConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPartnerConnectionRepository extends JpaRepository<UserPartnerConnection, Long> {

    List<UserPartnerConnection> findByUserId(Long userId);

    List<UserPartnerConnection> findByPartnerId(Long partnerId);

    List<UserPartnerConnection> findUserPartnerConnectionByPartnerId(Long partnerId);

//    Optional<UserPartnerConnection> findByUserIdAndPartnerId(Long userId, Long partnerId);

//    boolean existsByUserIdAndPartnerId(Long userId, Long partnerId);

    List<UserPartnerConnection> findByUserIdAndPartnerId(Long userId, Long partnerId);
}

