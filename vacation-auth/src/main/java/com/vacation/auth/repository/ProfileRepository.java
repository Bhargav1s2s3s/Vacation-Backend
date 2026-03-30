package com.vacation.auth.repository;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    boolean existsByEmailId(String email);

    Optional<ProfileEntity> findByUser(UserEntity user);
}
