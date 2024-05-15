package com.eteration.simplebanking.controller;

import lombok.Data;
import lombok.Getter;
import java.util.UUID;


@Data
@Getter
public class TransactionStatus {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    private String status;
    private String approvalCode;

    public TransactionStatus(String status) {
        this.status = status;
        this.approvalCode =  UUID.randomUUID().toString();
    }

    public TransactionStatus() {
    }

}
