package com.dino.ventures.backend.wallet.service.enums;

public enum TransactionType {
    TOPUP("0"),
    BONUS("1"),
    SPEND("2");

    private final String code;
    TransactionType(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
    public static TransactionType fromCode(String code){
        for(TransactionType type : TransactionType.values()){
            if(type.getCode().equals(code)){
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TransactionType code: " + code);
    }
}
