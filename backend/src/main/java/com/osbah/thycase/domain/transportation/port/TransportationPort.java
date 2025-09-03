package com.osbah.thycase.domain.transportation.port;

import com.osbah.thycase.domain.transportation.model.Transportation;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface TransportationPort {

    Slice<Transportation> getAllTransportations(int pageNum, int pageSize);

    Transportation createTransportation(Transportation transportation);

    Optional<Transportation> getTransportation(long id);

    void deleteTransportation(long id);

    Optional<Transportation> updateTransportation(long id, Transportation transportation);

    List<Transportation> findFlightTransportationsByOrigin(long originId, Integer dow);

    List<Transportation> findNonFlightTransportationsByOrigin(long originId, Integer dow);
}
