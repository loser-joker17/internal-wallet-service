package com.dino.ventures.backend.wallet.service.repository;

import com.dino.ventures.backend.wallet.service.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,Long> {
}
