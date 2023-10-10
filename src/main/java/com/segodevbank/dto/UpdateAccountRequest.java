package com.segodevbank.dto;

import com.segodevbank.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAccountRequest {

    private Integer balance;
    private Currency currency;
    private Long customerId;
}
