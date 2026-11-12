package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDetailResponse {
    private String name;
    private String imageUrl;
    private String description;
    private String director;
    private String performer;
    private String genre;
    private Integer duration;
    private String language;
    private Integer limitAge;
    private LocalDate releaseDate;
}