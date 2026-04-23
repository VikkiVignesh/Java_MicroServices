package com.fitness.userservice.repository;

import com.fitness.userservice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users,String> {
    boolean existsByEmail(String email);

    Boolean existsByKeyCloakId(String userId);

    Users findByEmail(String email);
}
