package com.eteration.simplebanking.model.http;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateAccountRequest {

    @NotEmpty
    private String ownerName;

    @NotEmpty
    private String accountNumber;
}
