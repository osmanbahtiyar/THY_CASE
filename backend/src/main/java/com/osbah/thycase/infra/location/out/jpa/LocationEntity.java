package com.osbah.thycase.infra.location.out.jpa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "LOCATION")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Location entity representing airports, city centers, and other transportation hubs")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loc_seq")
    @SequenceGenerator(name = "loc_seq", sequenceName = "LOCATION_SEQ", allocationSize = 50)
    private Long id;

    @Column(name = "CODE", unique = true)
    @NotBlank(message = "Code is required")
    @Size(max = 16, message = "Code must not exceed 16 characters")
    private String code;

    @Column(name = "LOCATION_NAME", nullable = false)
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Column(name = "COUNTRY", nullable = false)
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Column(name = "CITY", nullable = false)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
}