package com.vacation.repository;

import com.vacation.entity.ProfileEntity;
import com.vacation.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    boolean existsByEmailId(String email);

    Optional<ProfileEntity> findByUser(UserEntity user);
}
