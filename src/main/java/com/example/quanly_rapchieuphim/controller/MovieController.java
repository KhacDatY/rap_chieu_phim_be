package com.example.quanly_rapchieuphim.controller;

import com.example.quanly_rapchieuphim.dto.request.ApiResponse;
import com.example.quanly_rapchieuphim.dto.response.MovieDetailResponse;
import com.example.quanly_rapchieuphim.dto.response.MovieSearchResponse;
import com.example.quanly_rapchieuphim.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/search")
    public ApiResponse<List<MovieSearchResponse>> searchMovies(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.<List<MovieSearchResponse>>builder()
                .result(movieService.searchMovies(keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieDetailResponse> getMovieDetail(@PathVariable UUID id) {
        return ApiResponse.<MovieDetailResponse>builder()
                .result(movieService.getMovieDetail(id))
                .build();
    }
}