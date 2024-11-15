package com.ReachU.ServiceBookingSystem.repository;

import com.ReachU.ServiceBookingSystem.dto.PartnerDTO;
import com.ReachU.ServiceBookingSystem.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByEmail(String email);
    Optional<Partner> findFirstByEmail(String email);
    List<Partner> findByService(String service);

    @Query("SELECT p FROM Partner p LEFT JOIN FETCH p.connections")
    List<Partner> findAllWithConnections();


}
