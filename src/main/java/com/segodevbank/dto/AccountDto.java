package com.segodevbank.dto;

import com.segodevbank.model.Currency;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AccountDto implements Serializable {

    private Long id;
    private Integer balance;
    private Currency currency;
    private Long customerId;

}
