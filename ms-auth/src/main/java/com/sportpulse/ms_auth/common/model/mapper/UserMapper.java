package com.sportpulse.ms_auth.common.model.mapper;

import com.sportpulse.ms_auth.common.constants.JwtConstants;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.common.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(com.sportpulse.ms_auth.common.enums.UserRole.USER)")
    @Mapping(target = "password", ignore = true)
    UserEntity toUserEntity(RegisterRequest request);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "tokenType", constant = JwtConstants.TOKEN_TYPE_BEARER)
    @Mapping(target = "expiresIn", constant = JwtConstants.EXPIRES_IN_SECONDS_DEFAULT)
    @Mapping(target = "userId", ignore = true)
    TokenResponse toTokenResponse(String token);
}
