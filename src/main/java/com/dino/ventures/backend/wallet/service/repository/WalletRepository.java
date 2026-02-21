package com.dino.ventures.backend.wallet.service.repository;

import com.dino.ventures.backend.wallet.service.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.asset.id = :assetId")
    Optional<Wallet> findForUpdate(Long userId, Long assetId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.systemAccount = true AND w.asset.id = :assetId")
    Wallet findSystemWallet(Long assetId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Wallet lockById(Long id);
}
