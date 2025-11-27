package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.Address;
import com.farmporject.backend.trade.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreateTimeDesc(userId);
    }

    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    public Address createAddress(Address address) {
        // 如果设置为默认地址，取消其他默认地址
        if (address.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(address.getUserId())
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setIsDefault(false);
                        addressRepository.save(defaultAddress);
                    });
        }
        return addressRepository.save(address);
    }

    public Address updateAddress(Long id, Address addressDetails) {
        return addressRepository.findById(id)
                .map(address -> {
                    if (addressDetails.getReceiverName() != null) address.setReceiverName(addressDetails.getReceiverName());
                    if (addressDetails.getReceiverPhone() != null) address.setReceiverPhone(addressDetails.getReceiverPhone());
                    if (addressDetails.getProvince() != null) address.setProvince(addressDetails.getProvince());
                    if (addressDetails.getCity() != null) address.setCity(addressDetails.getCity());
                    if (addressDetails.getDistrict() != null) address.setDistrict(addressDetails.getDistrict());
                    if (addressDetails.getDetailAddress() != null) address.setDetailAddress(addressDetails.getDetailAddress());
                    if (addressDetails.getPostalCode() != null) address.setPostalCode(addressDetails.getPostalCode());
                    if (addressDetails.getIsDefault() != null) {
                        // 如果设置为默认地址，取消其他默认地址
                        if (addressDetails.getIsDefault()) {
                            addressRepository.findByUserIdAndIsDefaultTrue(address.getUserId())
                                    .ifPresent(defaultAddress -> {
                                        if (!defaultAddress.getId().equals(id)) {
                                            defaultAddress.setIsDefault(false);
                                            addressRepository.save(defaultAddress);
                                        }
                                    });
                        }
                        address.setIsDefault(addressDetails.getIsDefault());
                    }
                    return addressRepository.save(address);
                })
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }

    public void deleteAddress(Long userId, Long id) {
        addressRepository.deleteByUserIdAndId(userId, id);
    }

    public Optional<Address> getDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
}