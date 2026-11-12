package com.example.quanly_rapchieuphim.entity;

import com.example.quanly_rapchieuphim.enums.DiscountScope;
import com.example.quanly_rapchieuphim.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountScope scope;

    @Column(nullable = false)
    private Double value;

    private Double maxDiscount;
    private Double minValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
