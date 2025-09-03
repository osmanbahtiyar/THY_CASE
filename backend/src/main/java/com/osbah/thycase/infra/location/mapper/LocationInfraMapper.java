package com.osbah.thycase.infra.location.mapper;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.infra.location.in.rest.request.LocationUpsertPayload;
import com.osbah.thycase.infra.location.in.rest.response.LocationPageResponse;
import com.osbah.thycase.infra.location.in.rest.response.LocationResponse;
import com.osbah.thycase.infra.location.out.jpa.LocationEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LocationInfraMapper {

    LocationInfraMapper INSTANCE = Mappers.getMapper(LocationInfraMapper.class);

    Location toLocation(LocationEntity locationEntity);

    LocationEntity toLocationEntity(Location location);

    @Mapping(target = "locations", source = "content", qualifiedByName = "toLocationResponseList")
    @Mapping(target = "pageNum", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "hasNext", source = ".", qualifiedByName = "sliceHasNext")
    LocationPageResponse toLocationPageResponse(Slice<Location> locationSlice);

    @Mapping(target = "locationCode", source = "code")
    LocationResponse toLocationResponse(Location location);

    @Named("toLocationResponseList")
    List<LocationResponse> toLocationResponseList(List<Location> locations);

    @Named("sliceHasNext")
    default boolean sliceHasNext(Slice<?> slice) {
        return slice.hasNext();
    }

    @Mapping(target = "code", source = "payload.locationCode")
    Location toLocation(LocationUpsertPayload payload, long id);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "locationCode")
    Location toLocationWithEmptyId(LocationUpsertPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(Location domain, @MappingTarget LocationEntity entity);
}
