package com.dino.ventures.backend.wallet.service.service.impl;

import com.dino.ventures.backend.wallet.service.dto.request.TransferRequest;
import com.dino.ventures.backend.wallet.service.dto.response.BalanceResponse;
import com.dino.ventures.backend.wallet.service.dto.response.CommonSuccessResponse;
import com.dino.ventures.backend.wallet.service.entity.LedgerEntry;
import com.dino.ventures.backend.wallet.service.entity.Transaction;
import com.dino.ventures.backend.wallet.service.entity.Wallet;
import com.dino.ventures.backend.wallet.service.enums.TransactionStatus;
import com.dino.ventures.backend.wallet.service.exception.DuplicateTransactionException;
import com.dino.ventures.backend.wallet.service.exception.InsufficientBalanceException;
import com.dino.ventures.backend.wallet.service.exception.WalletNotFoundException;
import com.dino.ventures.backend.wallet.service.repository.LedgerEntryRepository;
import com.dino.ventures.backend.wallet.service.repository.TransactionRepository;
import com.dino.ventures.backend.wallet.service.repository.WalletRepository;
import com.dino.ventures.backend.wallet.service.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public WalletServiceImpl(WalletRepository walletRepository,TransactionRepository transactionRepository,LedgerEntryRepository ledgerEntryRepository){
        this.walletRepository=walletRepository;
        this.transactionRepository=transactionRepository;
        this.ledgerEntryRepository=ledgerEntryRepository;
    }

    @Transactional
    public CommonSuccessResponse processTransaction(TransferRequest request) {

        if (transactionRepository.existsByReferenceId(request.getReferenceId())) {
            throw new DuplicateTransactionException("Duplicate request");
        }

        Wallet userWallet = walletRepository
                .findForUpdate(request.getUserId(), request.getAssetId())
                .orElseThrow(() -> new WalletNotFoundException("User wallet not found"));

        Wallet treasuryWallet = walletRepository
                .findSystemWallet(request.getAssetId());

        Wallet first = userWallet.getId() < treasuryWallet.getId() ? userWallet : treasuryWallet;
        Wallet second = userWallet.getId() < treasuryWallet.getId() ? treasuryWallet : userWallet;

        walletRepository.lockById(first.getId());
        walletRepository.lockById(second.getId());

        BigDecimal amount = request.getAmount();
        switch (request.getType()) {
            case TOPUP, BONUS -> {
                userWallet.setBalance(userWallet.getBalance().add(amount));
                treasuryWallet.setBalance(treasuryWallet.getBalance().subtract(amount));
            }

            case SPEND -> {
                if (userWallet.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                userWallet.setBalance(userWallet.getBalance().subtract(amount));
                treasuryWallet.setBalance(treasuryWallet.getBalance().add(amount));
            }
        }

        walletRepository.save(userWallet);
        walletRepository.save(treasuryWallet);

        Transaction txn = transactionRepository.save(
                Transaction.builder()
                        .referenceId(request.getReferenceId())
                        .type(request.getType())
                        .amount(amount)
                        .status(TransactionStatus.SUCCESS)
                        .build()
        );

        recordLedger(userWallet, txn);
        recordLedger(treasuryWallet, txn);

        return new CommonSuccessResponse("Processed");
    }

    @Override
    @Transactional
    public BalanceResponse getBalance(Long userId, Long assetId) {
        Wallet wallet = walletRepository
                .findForUpdate(userId, assetId)
                .orElseThrow();

        return new BalanceResponse(userId, assetId, wallet.getBalance());
    }

    public void recordLedger(Wallet wallet, Transaction transaction) {

        LedgerEntry entry = LedgerEntry.builder()
                .wallet(wallet)
                .transaction(transaction)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(Instant.now())
                .build();

        ledgerEntryRepository.save(entry);
    }
}
