package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.TicketPrice;
import com.example.quanly_rapchieuphim.enums.DayType;
import com.example.quanly_rapchieuphim.enums.RoomType;
import com.example.quanly_rapchieuphim.enums.SeatType;
import com.example.quanly_rapchieuphim.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, UUID> {
    boolean existsByRoomTypeAndSeatTypeAndDayTypeAndTimeSlot(RoomType room, SeatType seat, DayType day, TimeSlot time);
    Optional<TicketPrice> findByRoomTypeAndSeatTypeAndDayTypeAndTimeSlot(RoomType roomType, SeatType seatType, DayType dayType, TimeSlot timeSlot);
}