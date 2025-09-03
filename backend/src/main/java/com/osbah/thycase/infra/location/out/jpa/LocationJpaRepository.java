package com.osbah.thycase.infra.location.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationJpaRepository extends JpaRepository<LocationEntity, Long> {
    LocationEntity findByCode(String code);

    boolean existsByCode(String code);

}
