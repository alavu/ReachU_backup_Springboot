package com.ReachU.ServiceBookingSystem.services.authentication.authServiceImpl;

import com.ReachU.ServiceBookingSystem.configs.CustomUserDetailService;
import com.ReachU.ServiceBookingSystem.dto.LoginDTO;
import com.ReachU.ServiceBookingSystem.dto.PartnerDTO;
import com.ReachU.ServiceBookingSystem.dto.SignupRequestDTO;
import com.ReachU.ServiceBookingSystem.dto.UserDto;
import com.ReachU.ServiceBookingSystem.entity.*;
import com.ReachU.ServiceBookingSystem.enums.EmailTemplateName;
import com.ReachU.ServiceBookingSystem.enums.UserRole;
import com.ReachU.ServiceBookingSystem.repository.*;
import com.ReachU.ServiceBookingSystem.response.LoginResponse;
import com.ReachU.ServiceBookingSystem.services.authentication.AuthService;
import com.ReachU.ServiceBookingSystem.services.authentication.EmailService;
import com.ReachU.ServiceBookingSystem.services.client.clientService.ClientService;
import com.ReachU.ServiceBookingSystem.utill.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PartnerRepository partnerRepository;

    private final TokenRepository tokenRepository;

    private final ClientService clientService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final CustomUserDetailService customUserDetailService;

    private final AdminRepository adminRepository;

    @Value("${avatar.img}")
    private String avatarImg;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDto signupClient(SignupRequestDTO signupRequestDTO) {
        User user = new User();

        user.setName(signupRequestDTO.getName());
        user.setLastname(signupRequestDTO.getLastname());
        user.setEmail(signupRequestDTO.getEmail());
        user.setPhone(signupRequestDTO.getPhone());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequestDTO.getPassword()));
        user.setEnabled(true); //Enable true only after otp verification
        user.setBlocked(false);
        user.setImageUrl(avatarImg);
        user.setUserRole(UserRole.CLIENT);
        user.set_blocked_by_admin(false);
        user.set_google_logged_in(false);
        sendValidationEmail(user);
        return userRepository.save(user).getDto();
    }

    public Boolean presentByEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @Override
    public PartnerDTO signupPartner(PartnerDTO partnerDTO) {
        Partner partner = new Partner();
        partner.setName(partnerDTO.getName());
        partner.setLastname(partnerDTO.getLastname());
        partner.setEmail(partnerDTO.getEmail());
        partner.setPhone(partnerDTO.getPhone());
        partner.setPassword(new BCryptPasswordEncoder().encode(partnerDTO.getPassword()));
        partner.setEnabled(true);
        partner.setBlocked(false);
        partner.set_blocked_by_admin(false);
        partner.setUserRole(UserRole.PARTNER);
        partner.setService(partnerDTO.getService());

//        List<ServiceEntity> selectedServices = serviceRepository.findAllById(partnerDTO.getServiceIds());
//        partner.setSelectServices(selectedServices);

        sendValidationEmail(partner);
        partnerRepository.save(partner);
        return partnerDTO;
    }

    @Override
    public LoginResponse authenticate(LoginDTO loginDTO) {
        authenticateUser(loginDTO);

        Optional<User> optionalUser = userRepository.findUserByEmail(loginDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
//            if (user.isBlocked() || !user.isEnabled()) {
//                throw new RuntimeException("User is blocked");
//            }
            final UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDTO.getEmail());
            Map<String, String> tokens = jwtUtil.createTokens(userDetails);

            return LoginResponse.builder()
                    .token(tokens.get("accessToken"))
                    .userId(user.getId())
                    .refreshToken(tokens.get("refreshToken"))
                    .userRole(user.getUserRole())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .blocked(user.isBlocked())
                    .build();

        }
        Optional<Admin> optionalAdmin = adminRepository.findFirstByEmail(loginDTO.getEmail());
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            final UserDetails adminDetails = customUserDetailService.loadUserByUsername(loginDTO.getEmail());
            Map<String, String> tokens = jwtUtil.createTokens(adminDetails);

            return LoginResponse.builder()
                    .token(tokens.get("accessToken"))
                    .refreshToken(tokens.get("refreshToken"))
                    .userId(1L)
                    .userRole(admin.getUserRole())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
        }
        Optional<Partner> optionalPartner = partnerRepository.findFirstByEmail(loginDTO.getEmail());
        if (optionalPartner.isPresent()) {
            Partner partner = optionalPartner.get();

            // Check if the partner is blocked or not enabled
//            if (partner.isBlocked() || !partner.isEnabled()) {
//                throw new RuntimeException("Partner is blocked");
//            }

            final UserDetails partnerDetails = customUserDetailService.loadUserByUsername(loginDTO.getEmail());
            Map<String, String> tokens = jwtUtil.createTokens(partnerDetails);

            return LoginResponse.builder()
                    .token(tokens.get("accessToken"))
                    .refreshToken(tokens.get("refreshToken"))
                    .userId(partner.getId())
                    .userRole(partner.getUserRole())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .blocked(partner.isBlocked())
                    .verified(partner.isVerified())
                    .build();
        }
        else {
            throw new RuntimeException("User, Partner or Admin not found");
        }
    }


    private void authenticateUser(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @Override
    public void activateAccount(String token) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        log.error("Token has expired: {}", token);
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }
        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        log.info("User {} activated successfully with token {}", user.getEmail(), token);
    }

    @Override
    public void resendActivationCode(String email) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is already activated
        if (user.isEnabled()) {
            throw new RuntimeException("User is already activated");
        }

        // Send a new activation email
        sendValidationEmail(user);
        log.info("Resent activation code to {}", email);
        user.setEnabled(false);
    }


//    private String generateAndSaveActivationToken(User user) {
//        // Generate a token
//        String generatedToken = generateActivationCode();
//        log.debug("Generated Token: " + generatedToken);  // Log the generated token
//        var token = Token.builder()
//                .token(generatedToken)
//                .createdAt(LocalDateTime.now())
//                .expiresAt(LocalDateTime.now().plusMinutes(1))
//                .user(user)
//                .build();
//        tokenRepository.save(token);
//        return generatedToken;
//    }

//
//    private void sendValidationEmail(User user) {
//        var newToken = generateAndSaveActivationToken(user);
//        //Send email
//        emailService.sendEmail(
//                user.getEmail(),
//                user.getName(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                activationUrl,
//                newToken,
//                "Account activation"
//        );
//    }

    /*Valdation Email Testing*/
    private void sendValidationEmail(User user) {
        sendValidationEmailCommon(user.getEmail(), user.getName(), user.getId(), user, null);
    }

    private void sendValidationEmail(Partner partner) {
        sendValidationEmailCommon(partner.getEmail(), partner.getName(), partner.getId(), null, partner);
    }

    private void sendValidationEmailCommon(String email, String name, Long id, User user, Partner partner) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (user == null && partner == null) {
            throw new IllegalArgumentException("Both user and partner cannot be null");
        }

        var newToken = generateAndSaveActivationToken(user, partner);

        // Assuming activationUrl is properly defined and not null
        emailService.sendEmail(
                email,
                name,
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user, Partner partner) {
        // Generate a token
        String generatedToken = generateActivationCode();
        log.debug("Generated Token: " + generatedToken);  // Log the generated token

        // TokenBuilder setup
        Token.TokenBuilder tokenBuilder = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1));

        if (user != null) {
            tokenBuilder.user(user);
        } else if (partner != null) {
            tokenBuilder.partner(partner);
        } else {
            throw new IllegalArgumentException("Both user and partner are null");
        }

        // Build and save the token
        Token token = tokenBuilder.build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

            for (int i = 0; i < 6; i++) {
                int randomIndex = secureRandom.nextInt(characters.length());
                codeBuilder.append(characters.charAt(randomIndex));
            }

            return codeBuilder.toString();
        }
    }
