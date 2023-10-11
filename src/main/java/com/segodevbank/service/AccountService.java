package com.segodevbank.service;

import com.segodevbank.dto.*;
import com.segodevbank.exception.AccountNotFoundException;
import com.segodevbank.exception.CustomerNotFoundException;
import com.segodevbank.exception.DepositMoneyException;
import com.segodevbank.exception.WithdrawMoneyException;
import com.segodevbank.model.Account;
import com.segodevbank.model.City;
import com.segodevbank.model.Customer;
import com.segodevbank.repository.AccountRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Value("${sample.rabbitmq.queue}")
    private String queueName;

    @Value("${sample.rabbitmq.routingKey}")
    private String routingKey;

    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final AccountDtoConverter accountDtoConverter;

    private final DirectExchange directExchange;
    private final AmqpTemplate rabbitTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public AccountService(AccountRepository accountRepository,
                          CustomerService customerService,
                          AccountDtoConverter accountDtoConverter,
                          DirectExchange directExchange,
                          RabbitTemplate rabbitTemplate,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
        this.accountDtoConverter = accountDtoConverter;
        this.kafkaTemplate = kafkaTemplate;
        this.directExchange = directExchange;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AccountDto createAccount(CreateAccountRequest accountRequest) {

        Customer customer = customerService.getCustomerById(accountRequest.getCustomerId());
        if (customer.getId() == null) {
            throw new CustomerNotFoundException("The Account Could Not Be Created " +
                    "Because It Is Not An Existing Customer!");
        }

        Account account = Account.builder()
                .id(accountRequest.getId())
                .customerId(accountRequest.getCustomerId())
                .currency(accountRequest.getCurrency())
                .balance(accountRequest.getBalance())
                .city(City.valueOf(accountRequest.getCity().name()))
                .build();

        return accountDtoConverter.getConvertAccount(accountRepository.save(account));
    }


    public AccountDto updateAccount(UpdateAccountRequest accountRequest, Long id) {

        Optional<Account> accountOptional = accountRepository.findById(id);

        accountOptional.ifPresent(account -> {
            account.setBalance(accountRequest.getBalance());
            account.setCurrency(accountRequest.getCurrency());
            account.setCustomerId(accountRequest.getCustomerId());
            accountRepository.save(account);
        });

        return accountOptional
                .map(accountDtoConverter::getConvertAccount)
                .orElseThrow(
                        () -> new RuntimeException("Update failed!")
                );
    }


    public List<AccountDto> getAllCustomers() {

        List<Account> accountList = accountRepository.findAll();

        return accountList.stream()
//              .map(account -> accountDtoConverter.getConvertAccount(account))
                .map(accountDtoConverter::getConvertAccount)
                .collect(Collectors.toList());

    }


    public AccountDto getAccountById(Long id) {

        return accountRepository.findById(id)
                .map(accountDtoConverter::getConvertAccount)
                .orElseThrow(
                        () -> new AccountNotFoundException("Account Not Found!")
                );

    }

    public void deleteAccount(Long id) {

        accountRepository.findById(id)
                .orElseThrow(
                        () -> new AccountNotFoundException("The account could " +
                                "not be deleted because it could not be found!")
                );
        accountRepository.deleteById(id);
    }

    public AccountDto withdrawMoney(Long id, Integer amount) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        accountOptional.ifPresent(account -> {
            if (account.getBalance() > amount) {
                account.setBalance(account.getBalance() - amount);
                accountRepository.save(account);
            } else {
                System.out.println("Insufficient funds -> accountId : " + id + "balance : " + account.getBalance());
            }
        });
        return accountOptional.map(accountDtoConverter::getConvertAccount)
                .orElseThrow(
                        () -> new WithdrawMoneyException("Withdraw Failed")
                );
    }

    public AccountDto addMoney(Long id, Integer amount) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        accountOptional.ifPresent(account -> {
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
        });
        return accountOptional.map(accountDtoConverter::getConvertAccount)
                .orElseThrow(
                        () -> new DepositMoneyException("Deposit Failed")
                );
    }

    public void transferMoney(MoneyTransferRequest transferRequest) {
        rabbitTemplate.convertAndSend(directExchange.getName(), routingKey, transferRequest);
    }


    @RabbitListener(queues = "${sample.rabbitmq.queue}")
    public void transferMoneyMessage(MoneyTransferRequest transferRequest) {
        Optional<Account> accountOptional = accountRepository.findById(transferRequest.getFromId());
        accountOptional.ifPresentOrElse(account -> {
                    if (account.getBalance() > transferRequest.getAmount()) {
                        account.setBalance(account.getBalance() - transferRequest.getAmount());
                        accountRepository.save(account);
                        rabbitTemplate.convertAndSend(directExchange.getName(), "secondRoute", transferRequest);
                    } else {
                        System.out.println("Insufficient funds -> accountId: " + transferRequest.getFromId() + " balance: " + account.getBalance() + " amount: " + transferRequest.getAmount());
                    }
                },
                () -> System.out.println("Account not found")
        );
    }

    @RabbitListener(queues = "secondStepQueue")
    public void updateReceiverAccount(MoneyTransferRequest transferRequest) {
        Optional<Account> accountOptional = accountRepository.findById(transferRequest.getToId());
        accountOptional.ifPresentOrElse(account -> {
                    account.setBalance(account.getBalance() + transferRequest.getAmount());
                    accountRepository.save(account);
                    rabbitTemplate.convertAndSend(directExchange.getName(), "thirdRoute", transferRequest);
                },
                () -> {
                    System.out.println("Receiver Account not found");
                    Optional<Account> senderAccount = accountRepository.findById(transferRequest.getFromId());
                    senderAccount.ifPresent(sender -> {
                        System.out.println("Money charge back to sender");
                        sender.setBalance(sender.getBalance() + transferRequest.getAmount());
                        accountRepository.save(sender);
                    });

                }
        );
    }

    @RabbitListener(queues = "thirdStepQueue")
    public void finalizeTransfer(MoneyTransferRequest transferRequest) {
        Optional<Account> accountOptional = accountRepository.findById(transferRequest.getFromId());
        accountOptional.ifPresentOrElse(account ->
                {
                    String notificationMessage = "Dear customer %s \n Your money transfer request has been succeed. Your new balance is %s";
                    System.out.println("Sender(" + account.getId() +") new account balance: " + account.getBalance());
                    String senderMessage = String.format(notificationMessage, account.getId(), account.getBalance());
                    kafkaTemplate.send("transfer-notification",  senderMessage);
                }, () -> System.out.println("Account not found")
        );

        Optional<Account> accountToOptional = accountRepository.findById(transferRequest.getToId());
        accountToOptional.ifPresentOrElse(account ->
                {
                    String notificationMessage = "Dear customer %s \n You received a money transfer from %s. Your new balance is %s";
                    System.out.println("Receiver(" + account.getId() +") new account balance: " + account.getBalance());
                    String receiverMessage = String.format(notificationMessage, account.getId(), transferRequest.getFromId(), account.getBalance());
                    kafkaTemplate.send("transfer-notification",  receiverMessage);
                },
                () -> System.out.println("Account not found")
        );


    }

}