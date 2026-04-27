package com.sportpulse.ms_auth.repository;

import com.sportpulse.ms_auth.common.model.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository <UserEntity, UUID> {

    Optional<UserEntity>findByEmail(String email);

}
