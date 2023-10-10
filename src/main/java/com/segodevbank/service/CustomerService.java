package com.segodevbank.service;

import com.segodevbank.dto.CreateCustomerRequest;
import com.segodevbank.dto.CustomerDto;
import com.segodevbank.dto.CustomerDtoConverter;
import com.segodevbank.dto.UpdateCustomerRequest;
import com.segodevbank.exception.CustomerNotFoundException;
import com.segodevbank.model.Address;
import com.segodevbank.model.City;
import com.segodevbank.model.Customer;
import com.segodevbank.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerDtoConverter customerDtoConverter;

    public CustomerService(CustomerRepository customerRepository, CustomerDtoConverter customerDtoConverter) {
        this.customerRepository = customerRepository;
        this.customerDtoConverter = customerDtoConverter;
    }

    public CustomerDto createCustomer(CreateCustomerRequest customerRequest) {

        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setSurname(customerRequest.getSurname());
        customer.setDateOfBirth(customerRequest.getDateOfBirth());
        customer.setMothersMaidenName(customerRequest.getMothersMaidenName());
        customer.setAddress(Address.builder()
                .city(City.valueOf(customerRequest.getAddress().getCity().name()))
                .postCode(customerRequest.getAddress().getPostCode())
                .addressDetails(customerRequest.getAddress().getAddressDetails())
                .build());
        customer.setCity(City.valueOf(customerRequest.getCity().name()));

        customerRepository.save(customer);

        return customerDtoConverter.getConvertCustomer(customer);
    }

    public List<CustomerDto> getAllCustomers() {

        List<Customer> customerList = customerRepository.findAll();

        return customerList.stream().map(customerDtoConverter::getConvertCustomer)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerDtoById(Long id) {
      /*  Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Customer Not Found"));
        return customerDtoConverter.getConvertCustomer(customer); */

        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional
                .map(customerDtoConverter::getConvertCustomer)
                .orElseThrow(
                        () -> new CustomerNotFoundException("Customer Not Found")
                );
    }


    public CustomerDto updateCustomer(UpdateCustomerRequest customerRequest, Long id) {

        Optional<Customer> customerOptional = customerRepository.findById(id);

        customerOptional.ifPresent(customer -> {
            customer.setName(customerRequest.getName());
            customer.setSurname(customerRequest.getSurname());
            customer.setMothersMaidenName(customerRequest.getMothersMaidenName());
            customer.setDateOfBirth(customerRequest.getDateOfBirth());
            customer.setCity(City.valueOf(customerRequest.getCity().name()));
            customer.setAddress(Address.builder()
                    .city(City.valueOf(customerRequest.getAddress().getCity().name()))
                    .postCode(customerRequest.getAddress().getPostCode())
                    .addressDetails(customerRequest.getAddress().getAddressDetails())
                    .build());
            customerRepository.save(customer);
        });

        return customerOptional
                .map(customerDtoConverter::getConvertCustomer)
                .orElseThrow(
                        () -> new CustomerNotFoundException("Update Failed")
                );
    }

    public void deleteCustomer(Long id) {
        customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("The Customer Could Not Be Deleted " +
                        "Because It Could Not Be Found!"));

        customerRepository.deleteById(id);
    }

    protected Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(
                        () -> new CustomerNotFoundException("Customer Not Found")
                );
    }
}
