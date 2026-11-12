package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String fullName;
    private LocalDate date;
    private String address;
    private String phoneNumber;
    private String email;
    private String role;

    private String memberRank;
    private Integer memberPoint;
}