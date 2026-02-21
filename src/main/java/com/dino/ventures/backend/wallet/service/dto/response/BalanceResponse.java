package com.dino.ventures.backend.wallet.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private Long userId;
    private Long assetId;
    private BigDecimal balance;
}
