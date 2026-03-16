package com.paiement.Backend.repository;

import com.paiement.Backend.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByUidRfidAndActifTrue(String uidRfid);
}