package com.example.quanly_rapchieuphim.service;

import com.example.quanly_rapchieuphim.dto.response.MovieDetailResponse;
import com.example.quanly_rapchieuphim.dto.response.MovieSearchResponse;
import com.example.quanly_rapchieuphim.entity.Movie;
import com.example.quanly_rapchieuphim.enums.MovieStatus;
import com.example.quanly_rapchieuphim.exception.AppException;
import com.example.quanly_rapchieuphim.exception.ErrorCode;
import com.example.quanly_rapchieuphim.mapper.MovieMapper;
import com.example.quanly_rapchieuphim.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieSearchResponse> searchMovies(String keyword) {
        List<Movie> movies;

        movies = movieRepository.findByNameContainingIgnoreCaseAndStatus(keyword.trim(),MovieStatus.NOW_SHOWING);

        return movies.stream()
                .map(movieMapper::toMovieSearchResponse)
                .toList();
    }

    public MovieDetailResponse getMovieDetail(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        return movieMapper.toMovieDetailResponse(movie);
    }
}