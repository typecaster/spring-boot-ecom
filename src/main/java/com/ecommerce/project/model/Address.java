package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters long")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name must be at least 5 characters long")
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "City name must be at least 4 characters long")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters long")
    private String State;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters long")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Zip code must be at least 5 characters long")
    private String zipCode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String state, String country, String zipCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        State = state;
        this.country = country;
        this.zipCode = zipCode;
    }
}