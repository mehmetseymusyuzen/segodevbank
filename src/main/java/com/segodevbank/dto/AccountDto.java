package com.segodevbank.dto;

import com.segodevbank.model.Currency;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AccountDto {

    private Long id;
    private Integer balance;
    private Currency currency;
    private Long customerId;

}
