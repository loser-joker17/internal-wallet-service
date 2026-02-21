package com.dino.ventures.backend.wallet.service.enums;

public enum TransactionStatus {

    SUCCESS("0"),
    FAILED("1");

    private final String code;
    TransactionStatus(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
    public static TransactionStatus fromCode(String code){
        for(TransactionStatus type : TransactionStatus.values()){
            if(type.getCode().equals(code)){
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TransactionStatus code: " + code);
    }
}
