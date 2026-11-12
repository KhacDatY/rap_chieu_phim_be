package com.example.quanly_rapchieuphim.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String fullName;
    private LocalDate date;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
}