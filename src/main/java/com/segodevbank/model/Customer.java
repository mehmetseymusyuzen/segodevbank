package com.segodevbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @SequenceGenerator(name = "seq_cus", allocationSize = 1)
    @GeneratedValue(generator = "seq_cus", strategy = GenerationType.SEQUENCE)

    private Long id;
    private String name;
    private String surname;
    private String mothersMaidenName;
    private Integer dateOfBirth;
    @Enumerated(EnumType.STRING)
    private City city;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

}