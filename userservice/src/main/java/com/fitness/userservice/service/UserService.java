package com.fitness.userservice.service;

import com.fitness.userservice.dtos.RegisterRequest;
import com.fitness.userservice.dtos.UserResponse;
import com.fitness.userservice.enums.UserRole;
import com.fitness.userservice.model.Users;
import com.fitness.userservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public UserResponse getUserProfile(String userId) {
        Users user=userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("User Not Found"));
        return convertToDto(user);
    }

    public UserResponse register(RegisterRequest request) {
        if(userRepo.existsByEmail(request.getEmail()))
        {
            Users existingUser=userRepo.findByEmail(request.getEmail());
            UserResponse response=convertToDto(existingUser);
            return response;
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setKeyCloakId(request.getKeyCloakId());
        user.setLastName(request.getLastName());

        Users saved = userRepo.save(user);
        return convertToDto(saved);
    }

    private UserResponse convertToDto(Users u) {
        UserResponse user = new UserResponse();
        user.setId(u.getId());
        user.setKeyCloakId(u.getKeyCloakId());
        user.setEmail(u.getEmail());
        user.setPassword(u.getPassword());
        user.setRole(u.getRole());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setCreatedAt(u.getCreatedAt());
        user.setUpdatedAt(u.getUpdatedAt());
        return user;
    }

    public Boolean validate(String userId) {
     return userRepo.existsByKeyCloakId(userId);
    }
}
