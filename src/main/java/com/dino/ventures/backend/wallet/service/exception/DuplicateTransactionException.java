package com.dino.ventures.backend.wallet.service.exception;

public class DuplicateTransactionException extends RuntimeException{
    public DuplicateTransactionException(String message) {
        super(message);
    }
}
