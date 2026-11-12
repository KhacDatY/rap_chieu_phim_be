package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, UUID> {
    boolean existsByScreeningRoomIdAndStartDate(UUID roomId, LocalDateTime startDate);
}