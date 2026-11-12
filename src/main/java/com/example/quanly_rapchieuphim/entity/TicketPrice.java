package com.example.quanly_rapchieuphim.entity;

import com.example.quanly_rapchieuphim.enums.DayType;
import com.example.quanly_rapchieuphim.enums.RoomType;
import com.example.quanly_rapchieuphim.enums.SeatType;
import com.example.quanly_rapchieuphim.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "ticket_prices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayType dayType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlot timeSlot;

    @Column(nullable = false)
    private Double price;
}
