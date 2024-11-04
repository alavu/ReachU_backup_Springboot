package com.ReachU.ServiceBookingSystem.services.user;

import com.ReachU.ServiceBookingSystem.dto.UserDto;
import com.ReachU.ServiceBookingSystem.entity.User;

import java.util.List;

public interface UserManagementService {

    User save(UserDto userDto);
     List<User> getAllUsers();
//    public List<UserDto> getAllUsers();

    User blockUser(Long userId);

    User unblockUser(Long userId);

    boolean isAdminBlockedUser(Long userId);

    Long findUserIdByEmail(String email);
}
