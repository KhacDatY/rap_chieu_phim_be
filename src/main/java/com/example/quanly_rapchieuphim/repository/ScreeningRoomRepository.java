package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.ScreeningRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ScreeningRoomRepository extends JpaRepository<ScreeningRoom, UUID> {
    boolean existsByName(String name);
}