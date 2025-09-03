package com.osbah.thycase.domain.transportation.usecase;

import com.osbah.thycase.domain.transportation.model.Transportation;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TransportationUseCase {

    Slice<Transportation> getAllTransportations(int pageNum, int pageSize);

    Optional<Transportation> getTransportation(long transportationId);

    Transportation createTransportation(Transportation transportation);

    void deleteTransportation(long transportationId);

    Transportation updateTransportation(long transportationId, Transportation transportation);
}
