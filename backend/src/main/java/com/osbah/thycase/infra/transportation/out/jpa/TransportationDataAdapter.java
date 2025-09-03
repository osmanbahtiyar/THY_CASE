package com.osbah.thycase.infra.transportation.out.jpa;

import com.osbah.thycase.domain.transportation.model.Transportation;
import com.osbah.thycase.domain.transportation.port.TransportationPort;
import com.osbah.thycase.infra.transportation.mapper.TransportationInfraMapper;
import com.osbah.thycase.shared.exception.ConflictException;
import com.osbah.thycase.shared.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class TransportationDataAdapter implements TransportationPort {

    private final TransportationJpaRepository transportationRepository;

    @Override
    public Slice<Transportation> getAllTransportations(int pageNum, int pageSize) {
        log.debug("TransportationDataAdapter: getAllTransportations called with pageNum={}, pageSize={}", pageNum, pageSize);
        return transportationRepository.findAll(PageRequest.of(pageNum, pageSize)).map(TransportationInfraMapper.INSTANCE::toTransportation);
    }

    @Override
    @Transactional
    public Transportation createTransportation(Transportation transportation) {
        log.debug("TransportationDataAdapter: createTransportation called with {}", transportation);
        checkTransportationAlreadyExists(transportation);
        return saveTransportation(transportation);
    }

    @Override
    public Optional<Transportation> getTransportation(long id) {
        log.debug("TransportationDataAdapter: getTransportation called with {}", id);
        return transportationRepository.findById(id).map(TransportationInfraMapper.INSTANCE::toTransportation);
    }

    @Override
    @Transactional
    public void deleteTransportation(long id) {
        log.debug("TransportationDataAdapter: deleteTransportation called with {}", id);
        transportationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Transportation> updateTransportation(long id, Transportation transportation) {
        return transportationRepository.findById(id).map(transportationEntity -> {
            TransportationInfraMapper.INSTANCE.updateEntityFromDomain(transportation, transportationEntity);
            TransportationEntity saved = transportationRepository.saveAndFlush(transportationEntity);
            return TransportationInfraMapper.INSTANCE.toTransportation(saved);
        });
    }

    @Override
    public List<Transportation> findFlightTransportationsByOrigin(long originId, Integer operatingDay) {
        return transportationRepository.findFlightTransportationsByOrigin(originId, operatingDay).stream()
                .map(TransportationInfraMapper.INSTANCE::toTransportation)
                .toList();
    }

    @Override
    public List<Transportation> findNonFlightTransportationsByOrigin(long originId, Integer operatingDay) {
        return transportationRepository.findNonFlightTransportationsByOrigin(originId, operatingDay).stream()
                .map(TransportationInfraMapper.INSTANCE::toTransportation)
                .toList();
    }

    private void checkTransportationAlreadyExists(Transportation transportation) {
        if (transportationRepository.existsByOriginLocation_IdAndDestinationLocation_IdAndTransportationType(transportation.getOriginLocation().getId(), transportation.getDestinationLocation().getId(), transportation.getTransportationType().name())) {
            log.debug("Transportation already exists");
            throw new ConflictException(ExceptionCode.TRANSPORTATION_CONFLICT.getErrorCode(), ExceptionCode.TRANSPORTATION_CONFLICT.formatMessage(transportation.getOriginLocation().getId().toString(), transportation.getDestinationLocation().getId().toString(), transportation.getTransportationType().name()));
        }
    }

    private Transportation saveTransportation(Transportation transportation) {
        TransportationEntity entity = TransportationInfraMapper.INSTANCE.toTransportationEntity(transportation);
        try {
            TransportationEntity savedLocation = transportationRepository.saveAndFlush(entity);
            log.info("Transportation saved with id:{}", savedLocation);
            return TransportationInfraMapper.INSTANCE.toTransportation(savedLocation);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition on unique origin_location_id, destination_location_id, transportation_type", e);
            throw new ConflictException(ExceptionCode.TRANSPORTATION_CONFLICT.getErrorCode(), ExceptionCode.TRANSPORTATION_CONFLICT.formatMessage(transportation.getOriginLocation().getId().toString(), transportation.getDestinationLocation().getId().toString(), transportation.getTransportationType().name()), e);
        }
    }
}
