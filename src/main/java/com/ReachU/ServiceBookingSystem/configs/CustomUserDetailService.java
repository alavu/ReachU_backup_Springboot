package com.ReachU.ServiceBookingSystem.configs;

import com.ReachU.ServiceBookingSystem.entity.Admin;
import com.ReachU.ServiceBookingSystem.entity.Partner;
import com.ReachU.ServiceBookingSystem.entity.User;
import com.ReachU.ServiceBookingSystem.enums.UserRole;
import com.ReachU.ServiceBookingSystem.repository.AdminRepository;
import com.ReachU.ServiceBookingSystem.repository.PartnerRepository;
import com.ReachU.ServiceBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> adminOpt = adminRepository.findFirstByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("admin" + admin);
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(UserRole.ADMIN.name()));
            return new org.springframework.security.core.userdetails.User(admin.getUsername(), admin.getPassword(), authorities);
        }
        Optional<User> userOpt = userRepository.findUserByEmail(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(UserRole.CLIENT.name()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        Optional<Partner> partnerOpt = partnerRepository.findByEmail(username);
        if (partnerOpt.isPresent()) {
            Partner partner = partnerOpt.get();
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(UserRole.PARTNER.name()));
            return new org.springframework.security.core.userdetails.User(partner.getEmail(), partner.getPassword(), authorities);
        }
        throw new UsernameNotFoundException("Username " + username + " not found");
    }
}
