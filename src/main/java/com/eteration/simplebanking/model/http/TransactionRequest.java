package com.eteration.simplebanking.model.http;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {

    @NotNull
    private Double amount;

    public TransactionRequest() {
        this.amount = 0.0;
    }

    public TransactionRequest(Double amount) {
        this.amount = amount;
    }
}
