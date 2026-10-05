package com.project.ecommerce.dto.address;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean defaultAddress;
}