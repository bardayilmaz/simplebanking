package com.eteration.simplebanking.model.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExceptionModel {

    private ZonedDateTime timestamp;
    private int statusCode;
    private String errorCode;
    private String error;
}
