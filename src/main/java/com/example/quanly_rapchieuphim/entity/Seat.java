package com.example.quanly_rapchieuphim.entity;

import com.example.quanly_rapchieuphim.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType type;

    @Column(nullable = false)
    private Integer rowPosition;

    @Column(nullable = false)
    private Integer columnPosition;

    @ManyToOne
    @JoinColumn(name = "screening_room_id", nullable = false)
    private ScreeningRoom screeningRoom;
}
