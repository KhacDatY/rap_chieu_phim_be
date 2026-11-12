package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieSearchResponse {
    private UUID id;
    private String name;
    private String imageUrl;
    private String genre;
    private Integer duration;
}