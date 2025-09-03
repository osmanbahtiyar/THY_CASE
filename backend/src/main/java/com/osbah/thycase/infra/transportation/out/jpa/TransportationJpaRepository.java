package com.osbah.thycase.infra.transportation.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportationJpaRepository extends JpaRepository<TransportationEntity, Long> {

    boolean existsByOriginLocation_IdAndDestinationLocation_IdAndTransportationType(Long originLocationId, Long destinationLocationId, String transportationType);

    @Query(value = """
            SELECT * FROM transportation t
            WHERE t.transportation_type = 'FLIGHT'
              AND t.origin_location_id = :originId
              AND (:operatingDay IS NULL OR t.operating_days @> ARRAY[:operatingDay]::int[])
            """, nativeQuery = true)
    List<TransportationEntity> findFlightTransportationsByOrigin(@Param("originId") long originId,
                                                                 @Param("operatingDay") Integer operatingDay);

    @Query(value = """
            SELECT * FROM transportation t
            WHERE t.transportation_type <> 'FLIGHT'
              AND t.origin_location_id = :originId
              AND (:operatingDay IS NULL OR t.operating_days @> ARRAY[:operatingDay]::int[])
            """, nativeQuery = true)
    List<TransportationEntity> findNonFlightTransportationsByOrigin(@Param("originId") long originId,
                                                                    @Param("operatingDay") Integer operatingDay);
}
