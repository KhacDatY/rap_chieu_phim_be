package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, UUID> {
    boolean existsByCode(String code);
}