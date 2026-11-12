package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.Movie;
import com.example.quanly_rapchieuphim.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    boolean existsByName(String name);
    List<Movie> findByStatus(MovieStatus status);
    List<Movie> findByNameContainingIgnoreCaseAndStatus(String name, MovieStatus status);
}