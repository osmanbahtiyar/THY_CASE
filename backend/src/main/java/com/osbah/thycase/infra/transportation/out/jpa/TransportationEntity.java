package com.osbah.thycase.infra.transportation.out.jpa;

import com.osbah.thycase.infra.location.out.jpa.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "TRANSPORTATION", uniqueConstraints = @UniqueConstraint(name = "uq_origin_dest_type", columnNames = {"origin_location_id", "destination_location_id", "transportation_type"}))
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Transportation entity representing transportation options from one location to another")
public class TransportationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transportation_seq")
    @SequenceGenerator(name = "transportation_seq", sequenceName = "TRANSPORTATION_SEQ", allocationSize = 50)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_location_id", nullable = false)
    private LocationEntity originLocation;

    @ManyToOne
    @JoinColumn(name = "destination_location_id", nullable = false)
    private LocationEntity destinationLocation;

    @Column(name = "transportation_type", nullable = false, length = 20)
    private String transportationType;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "operating_days", columnDefinition = "integer[]")
    private int[] operatingDays;
}
