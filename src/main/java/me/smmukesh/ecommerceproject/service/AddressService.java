package me.smmukesh.ecommerceproject.service;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.request.AddressDTO;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Address;
import me.smmukesh.ecommerceproject.model.User;
import me.smmukesh.ecommerceproject.repository.AddressRepository;
import me.smmukesh.ecommerceproject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository,
                          ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public AddressDTO createAddress(@Valid AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO,Address.class);

        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOs = addresses.
                stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return addressDTOs;
    }

    public AddressDTO getAddressById(Long addressId) {
        Address address =  addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    public List<AddressDTO> getAddressesOfUser(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return addressDTOs;
    }
}
