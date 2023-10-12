package com.segodevbank;

import com.segodevbank.model.*;
import com.segodevbank.repository.AccountRepository;
import com.segodevbank.repository.CustomerRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SegodevbankApplication implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public SegodevbankApplication(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SegodevbankApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String description,
                                 @Value("${application-version}") String version) {
        return new OpenAPI()
                .info(new Info()
                        .title("SegodevBank API")
                        .version(version)
                        .description(description)
                        .license(new License().name("SegodevBank API Licence")));
    }

    @Override
    public void run(String... args) throws Exception {
        Customer c1 = Customer.builder()
                .name("Mehmet")
                .surname("Seyhmus")
                .dateOfBirth(1999)
                .mothersMaidenName("MeSe")
                .address(Address.builder()
                        .city(City.DIYARBAKIR)
                        .postCode("2121")
                        .addressDetails("Home Address")
                        .build())
                .city(City.DIYARBAKIR)
                .build();

        Customer c2 = Customer.builder()
                .name("Mustafa")
                .surname("Reso")
                .dateOfBirth(2000)
                .mothersMaidenName("MuRe")
                .address(Address.builder()
                        .city(City.MARDIN)
                        .postCode("4747")
                        .addressDetails("Office Address")
                        .build())
                .city(City.DIYARBAKIR)
                .build();

        Customer c3 = Customer.builder()
                .name("Fuat")
                .surname("Beyto")
                .dateOfBirth(1998)
                .mothersMaidenName("FuBe")
                .address(Address.builder()
                        .city(City.SIRNAK)
                        .postCode("7373")
                        .addressDetails("Home Address")
                        .build())
                .city(City.SIRNAK)
                .build();

        customerRepository.saveAll(Arrays.asList(c1, c2, c3));


        Account a1 = Account.builder()
                .customerId(c1.getId())
                .balance(2000)
                .currency(Currency.GBP)
                .city(City.DIYARBAKIR)
                .build();

        Account a2 = Account.builder()
                .customerId(c2.getId())
                .balance(1500)
                .currency(Currency.EUR)
                .city(City.DIYARBAKIR)
                .build();

        Account a3 = Account.builder()
                .customerId(c3.getId())
                .balance(2500)
                .currency(Currency.USD)
                .city(City.SIRNAK)
                .build();

        accountRepository.saveAll(Arrays.asList(a1, a2, a3));
    }
}
