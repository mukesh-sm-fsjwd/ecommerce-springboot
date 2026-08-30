package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.request.AddressDTO;
import me.smmukesh.ecommerceproject.model.User;
import me.smmukesh.ecommerceproject.service.AddressService;
import me.smmukesh.ecommerceproject.utils.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    private final AddressService addressService;
    private final AuthUtils authUtils;

    public AddressController(AddressService addressService,AuthUtils authUtils) {
        this.addressService = addressService;
        this.authUtils = authUtils;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtils.loggedInUser();
        AddressDTO address = addressService.createAddress(addressDTO,user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(address);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress(){
        List<AddressDTO> addressDTOs = addressService.getAllAddresses();
        return ResponseEntity.status(HttpStatus.OK)
                .body(addressDTOs);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO address = addressService.getAddressById(addressId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(address);
    }
}
