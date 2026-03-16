package com.paiement.Backend.repository;

import com.paiement.Backend.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByUserIdOrderByDateDesc(Long userId);
}