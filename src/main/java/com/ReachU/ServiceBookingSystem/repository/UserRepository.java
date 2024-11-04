package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

    Optional<User> findById(Long id);

    User findByEmail(String email);

    Optional<User> findUserByEmail(String email);

    @Query("select u from User u")
    List<User> findAllUsers();

    @Query("select count (u) from User u")
    long countTotalCustomers();
}
