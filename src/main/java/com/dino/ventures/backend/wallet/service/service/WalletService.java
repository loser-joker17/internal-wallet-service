package com.dino.ventures.backend.wallet.service.service;

import com.dino.ventures.backend.wallet.service.dto.request.TransferRequest;
import com.dino.ventures.backend.wallet.service.dto.response.BalanceResponse;
import com.dino.ventures.backend.wallet.service.dto.response.CommonSuccessResponse;

public interface WalletService {
    CommonSuccessResponse processTransaction(TransferRequest request);
    BalanceResponse getBalance(Long userId, Long assetId);
}
