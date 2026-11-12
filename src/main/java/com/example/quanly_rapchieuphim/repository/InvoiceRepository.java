package com.example.quanly_rapchieuphim.repository;

import com.example.quanly_rapchieuphim.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.member " +
            "WHERE (:startDate IS NULL OR i.paymentDate >= :startDate) " +
            "AND (:endDate IS NULL OR i.paymentDate <= :endDate)")
    List<Invoice> findInvoicesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    List<Invoice> findByMemberIdOrderByPaymentDateDesc(UUID memberId);
}