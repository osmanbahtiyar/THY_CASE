package com.osbah.thycase.domain.location.model;

import lombok.Data;

@Data
public class Location {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String code;
}
