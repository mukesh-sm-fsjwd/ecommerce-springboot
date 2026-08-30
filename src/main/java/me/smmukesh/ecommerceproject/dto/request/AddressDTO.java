package me.smmukesh.ecommerceproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    @NotBlank
    @Size(min = 5,message = "The Street name must be at least 5 characters in size.")
    private String street;

    @NotBlank
    @Size(min = 5,message = "The Building name must be at least 5 characters in size.")
    private String buildingName;

    @NotBlank
    @Size(min = 4,message = "The City name must be at least 4 characters in size.")
    private String city;

    @NotBlank
    @Size(min = 2,message = "The State name must be at least 2 characters in size.")
    private String state;

    @NotBlank
    @Size(min = 2,message = "The Country name must be at least 2 characters in size.")
    private String country;

    @NotBlank
    @Size(min = 6,message = "The pin code must be at least 6 characters in size.")
    private String pincode;
}
