package com.example.quanly_rapchieuphim.mapper;

import com.example.quanly_rapchieuphim.dto.response.MovieDetailResponse;
import com.example.quanly_rapchieuphim.dto.response.MovieSearchResponse;
import com.example.quanly_rapchieuphim.entity.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieSearchResponse toMovieSearchResponse(Movie movie);
    MovieDetailResponse toMovieDetailResponse(Movie movie);
}