package com.project.ecommerce.service;

import com.project.ecommerce.dto.address.AddressRequest;
import com.project.ecommerce.dto.address.AddressResponse;
import com.project.ecommerce.entity.Address;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.AddressRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressResponse createAddress(
            Long userId,
            AddressRequest request) {

        User user = getUser(userId);

        if (request.isDefaultAddress()) {
            removeExistingDefaultAddress(userId);
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .defaultAddress(request.isDefaultAddress())
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {

        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse updateAddress(
            Long userId,
            Long addressId,
            AddressRequest request) {

        Address address = getUserAddress(userId, addressId);

        if (request.isDefaultAddress()) {
            removeExistingDefaultAddress(userId);
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(request.isDefaultAddress());

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse setDefaultAddress(
            Long userId,
            Long addressId) {

        Address address = getUserAddress(userId, addressId);

        removeExistingDefaultAddress(userId);

        address.setDefaultAddress(true);

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(
            Long userId,
            Long addressId) {

        Address address = getUserAddress(userId, addressId);

        addressRepository.delete(address);
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private Address getUserAddress(
            Long userId,
            Long addressId) {

        return addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));
    }

    private void removeExistingDefaultAddress(Long userId) {

        List<Address> addresses =
                addressRepository.findByUserId(userId);

        addresses.forEach(address -> {

            if (address.isDefaultAddress()) {
                address.setDefaultAddress(false);
            }
        });

        addressRepository.saveAll(addresses);
    }

    private AddressResponse toResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .defaultAddress(address.isDefaultAddress())
                .build();
    }
}