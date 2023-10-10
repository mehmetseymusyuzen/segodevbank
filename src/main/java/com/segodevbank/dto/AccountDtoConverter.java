package com.segodevbank.dto;

import com.segodevbank.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoConverter {

    public AccountDto getConvertAccount(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .customerId(account.getCustomerId())
                .build();
    }
}
