package com.segodevbank.dto;

import com.segodevbank.model.City;
import com.segodevbank.model.Currency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountRequest {

    private Long id;

    private Long customerId;
    private Integer balance;
    private City city;
    private Currency currency;

}
