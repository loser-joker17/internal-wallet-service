package com.dino.ventures.backend.wallet.service.controller;

import com.dino.ventures.backend.wallet.service.dto.request.TransferRequest;
import com.dino.ventures.backend.wallet.service.dto.response.BalanceResponse;
import com.dino.ventures.backend.wallet.service.dto.response.CommonSuccessResponse;
import com.dino.ventures.backend.wallet.service.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/wallet")
public class WalletController {

    private final WalletService walletService;
    public WalletController(WalletService walletService){
        this.walletService=walletService;
    }

    @PostMapping("/transactions")
    public CommonSuccessResponse processTransaction(@RequestBody TransferRequest request){
        return walletService.processTransaction(request);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam Long userId,@RequestParam Long assetId){
        return walletService.getBalance(userId,assetId);
    }
}
