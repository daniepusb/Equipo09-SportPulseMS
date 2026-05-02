// FixtureEventMapper.java
package com.sportpulse.ms_fixtures.mapper;


import com.sportpulse.ms_fixtures.dto.ApiFootballEventResponse;
import com.sportpulse.ms_fixtures.dto.FixtureEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FixtureEventMapper {

    @Mapping(source = "time.elapsed", target = "elapsed")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "detail", target = "detail")
    @Mapping(source = "team", target = "team")
    @Mapping(source = "player", target = "player")
    @Mapping(source = "assist", target = "assist")
    FixtureEventResponse toResponse(ApiFootballEventResponse event);
}