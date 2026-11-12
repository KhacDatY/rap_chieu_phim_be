package com.example.quanly_rapchieuphim.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest{
    @Size(min = 3, message = "INVALID_USERNAME")
    private String username;

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

    private String fullName;
    private LocalDate date;
    private String address;
    private String phoneNumber;

    @Email(message = "INVALID_EMAIL")
    private String email;

    private String role;
}