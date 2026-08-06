package me.smmukesh.ecommerceproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

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

    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
