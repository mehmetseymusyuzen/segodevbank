package com.segodevbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DepositMoneyException extends RuntimeException{

    public DepositMoneyException(String message) {
        super(message);
    }
}
