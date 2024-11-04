package com.ReachU.ServiceBookingSystem.services.user;

import com.ReachU.ServiceBookingSystem.dto.UserDto;
import com.ReachU.ServiceBookingSystem.entity.User;
import com.ReachU.ServiceBookingSystem.enums.UserRole;
import com.ReachU.ServiceBookingSystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    public User save(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // Consider encrypting the password before saving
        user.setName(userDto.getName());
        user.setLastname(userDto.getLastname());
        user.setPhone(userDto.getPhone());
        user.setUserRole(userDto.getRole());
        user.setEnabled(false);
        user.setBlocked(false);
        return userRepository.save(user);
    }

//    @Override
//    public List<UserDto> getAllUsers() {
//        return userRepository.findAll()
//                .stream()
//                .peek(user -> System.out.println("Fetched User: " + user)) // Log the User object
//                .filter(user -> user.getRole() != UserRole.ADMIN)
//                .map(this::convertToDto)  // Convert each User entity to UserDto
//                .peek(userDto -> System.out.println("Converted UserDto: " + userDto)) // Log the UserDto object
//                .toList();
//    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
//                .stream()
//                .peek(user -> System.out.println("Fetched User: " + user)) // Log the User object
//                .filter(user -> user.getUserRole() != UserRole.ADMIN)
//                .peek(user -> System.out.println("Filtered User: " + user)) // Log after filtering
//                .toList();
    }


    @Override
   public User blockUser(Long id) {
       User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
       user.setBlocked(true);
       userRepository.save(user);
       return user;
   }


   @Override
    public User unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(false);
        userRepository.save(user);
        return user;
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
//        userDto.setPassword(user.getPassword()); // Password should not be exposed in DTO, handle it carefully
        userDto.setName(user.getName());
        userDto.setLastname(user.getLastname());
        userDto.setPhone(user.getPhone());
        userDto.setRole(user.getUserRole());
        userDto.setBlocked(user.isBlocked()); // Set the actual blocked status from the user entity
        return userDto;
    }

    @Override
    public boolean isAdminBlockedUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.is_blocked_by_admin();
    }

    @Override
    public Long findUserIdByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }
}
