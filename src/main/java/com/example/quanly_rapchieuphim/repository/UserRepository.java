package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.User;
import com.example.quanly_rapchieuphim.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}