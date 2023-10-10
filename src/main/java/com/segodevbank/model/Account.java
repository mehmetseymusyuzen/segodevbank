package com.segodevbank.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Account {

    @Id
    @SequenceGenerator(name = "seq_acc", allocationSize = 1)
    @GeneratedValue(generator = "seq_acc", strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long customerId;
    private Integer balance;
    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private Currency currency;

}
