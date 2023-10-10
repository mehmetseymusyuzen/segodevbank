package com.segodevbank.service;

import com.segodevbank.dto.AccountDto;
import com.segodevbank.dto.AccountDtoConverter;
import com.segodevbank.dto.CreateAccountRequest;
import com.segodevbank.dto.UpdateAccountRequest;
import com.segodevbank.model.*;
import com.segodevbank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private CustomerService customerService;
    private AccountDtoConverter accountDtoConverter;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        customerService = Mockito.mock(CustomerService.class);
        accountDtoConverter = Mockito.mock(AccountDtoConverter.class);
        accountService = new AccountService(accountRepository, customerService, accountDtoConverter, restTemplate, directExchange, rabbitTemplate);
    }


    @Test
    void whenCreateAccountCalledWithValidRequest_itShouldReturnValidAccountDto() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .customerId(1L)
                .city(City.DIYARBAKIR)
                .balance(5000)
                .currency(Currency.GBP)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Mehmet")
                .surname("Seyhmus")
                .dateOfBirth(1999)
                .mothersMaidenName("MeSe")
                .addressType(Address.HOME_ADDRESS)
                .city(City.DIYARBAKIR)
                .build();


        Account account = Account.builder()
                .id(createAccountRequest.getId())
                .customerId(createAccountRequest.getCustomerId())
                .currency(createAccountRequest.getCurrency())
                .balance(createAccountRequest.getBalance())
                .city(City.valueOf(createAccountRequest.getCity().name()))
                .build();

        AccountDto accountDto = AccountDto.builder()
                .customerId(1L)
                .balance(5000)
                .currency(Currency.GBP)
                .build();

        Mockito.when(customerService.getCustomerById(1L)).thenReturn(customer);
        Mockito.when(accountRepository.save(account)).thenReturn(account);
        Mockito.when(accountDtoConverter.getConvertAccount(account)).thenReturn(accountDto);

        AccountDto result = accountService.createAccount(createAccountRequest);

        assertEquals(result, accountDto);

        Mockito.verify(customerService).getCustomerById(1L);
        Mockito.verify(accountRepository).save(account);
        Mockito.verify(accountDtoConverter).getConvertAccount(account);

    }

    @Test
    void whenUpdateAccountCalledWithId_itShouldReturnAccountDto() {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .customerId(2L)
                .balance(2000)
                .currency(Currency.GBP)
                .build();

        Account account = Account.builder()
                .customerId(updateAccountRequest.getCustomerId())
                .currency(updateAccountRequest.getCurrency())
                .balance(updateAccountRequest.getBalance())
                .build();

        AccountDto accountDto = AccountDto.builder()
                .id(1L)
                .customerId(2L)
                .balance(5000)
                .currency(Currency.GBP)
                .build();

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountDtoConverter.getConvertAccount(account)).thenReturn(accountDto);

        AccountDto result = accountService.updateAccount(updateAccountRequest, 1L);
        assertEquals(result, accountDto);

        Mockito.verify(accountRepository).findById(1L);
        Mockito.verify(accountDtoConverter).getConvertAccount(account);

    }
}