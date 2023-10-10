package com.segodevbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WithdrawMoneyException extends RuntimeException {

    public WithdrawMoneyException(String message) {
        super(message);
    }
}
