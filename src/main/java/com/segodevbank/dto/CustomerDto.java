package com.segodevbank.dto;

import com.segodevbank.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {

    private Long id;
    private String name;
    private String surname;
    private Integer dateOfBirth;
    private CityDto city;
    private Address address;

}
