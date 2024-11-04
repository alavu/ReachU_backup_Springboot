package com.ReachU.ServiceBookingSystem.entity;

import com.ReachU.ServiceBookingSystem.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "partner")
@Data
public class Partner implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lastname;

    private String email;

    private String password;

    private String phone;

    private String service;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] img;


    private boolean enabled;

    private boolean blocked;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "rejected", nullable = false)
    private boolean rejected;

    @Column(length = 500)
    private String rejectionReason;

    private boolean is_blocked_by_admin;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean is_google_logged_in;

    private String jobStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<Reservation> bookings;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserPartnerConnection> connections = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

}
