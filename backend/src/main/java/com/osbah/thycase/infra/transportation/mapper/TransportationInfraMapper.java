package com.osbah.thycase.infra.transportation.mapper;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.transportation.model.Transportation;
import com.osbah.thycase.infra.transportation.in.rest.request.TransportationUpsertPayload;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationLocationResponse;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationPageResponse;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationResponse;
import com.osbah.thycase.infra.transportation.out.jpa.TransportationEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Slice;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransportationInfraMapper {

    TransportationInfraMapper INSTANCE = Mappers.getMapper(TransportationInfraMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originLocation.id", source = "originLocationId")
    @Mapping(target = "destinationLocation.id", source = "destinationLocationId")
    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "integerToDayOfWeekSet")
    Transportation toTransportation(TransportationUpsertPayload transportationUpsertPayload);

    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "dayOfWeekSetToIntArray")
    TransportationEntity toTransportationEntity(Transportation transportation);

    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "intArrayToDayOfWeekSet")
    Transportation toTransportation(TransportationEntity transportationEntity);

    @Mapping(target = "originLocation", source = "originLocation", qualifiedByName = "toTransportationLocationResponse")
    @Mapping(target = "destinationLocation", source = "destinationLocation", qualifiedByName = "toTransportationLocationResponse")
    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "dayOfWeekSetToIntegerList")
    TransportationResponse toTransportationResponse(Transportation transportation);

    @Mapping(target = "transportations", source = "content", qualifiedByName = "toTransportationResponseList")
    @Mapping(target = "pageNum", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "hasNext", source = ".", qualifiedByName = "sliceHasNext")
    TransportationPageResponse toTransportationPageResponse(Slice<Transportation> transportationSlice);

    @Named("toTransportationResponseList")
    List<TransportationResponse> toTransportationResponseList(Collection<Transportation> transportations);

    @Named("sliceHasNext")
    default boolean sliceHasNext(Slice<?> slice) {
        return slice.hasNext();
    }

    @Named("toTransportationLocationResponse")
    @Mapping(target = "locationCode", source = "code")
    TransportationLocationResponse toTransportationLocationResponse(Location location);

    @Named("integerToDayOfWeekSet")
    default Set<DayOfWeek> integerToDayOfWeekSet(Collection<Integer> integers) {
        return integers.stream().sorted().map(DayOfWeek::of).collect(Collectors.toSet());
    }

    @Named("dayOfWeekSetToIntegerList")
    default List<Integer> dayOfWeekSetToIntegerList(Set<DayOfWeek> dayOfWeeks) {
        return dayOfWeeks.stream().map(DayOfWeek::getValue).sorted().toList();
    }

    @Named("intArrayToDayOfWeekSet")
    default Set<DayOfWeek> intArrayToDayOfWeekSet(int[] arr) {
        if (arr == null || arr.length == 0) {
            return Set.of();
        }
        return Arrays.stream(arr).sorted()
                .mapToObj(DayOfWeek::of)
                .collect(Collectors.toSet());
    }

    @Named("dayOfWeekSetToIntArray")
    default int[] dayOfWeekSetToIntArray(Set<DayOfWeek> dayOfWeeks) {
        if (dayOfWeeks == null || dayOfWeeks.isEmpty()) {
            return new int[0];
        }
        return dayOfWeeks.stream().sorted()
                .mapToInt(DayOfWeek::getValue)
                .sorted()
                .toArray();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "dayOfWeekSetToIntArray")
    void updateEntityFromDomain(Transportation domain, @MappingTarget TransportationEntity entity);


}
