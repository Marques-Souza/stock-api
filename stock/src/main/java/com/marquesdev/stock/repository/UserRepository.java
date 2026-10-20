package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String username);


    @Query("select u.profile from User u where u.email like :email")
    Profile findProfileByEmail(String email);
}
