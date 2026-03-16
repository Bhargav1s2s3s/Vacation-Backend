package com.vacation.auth.repository;

import com.vacation.auth.entity.RefreshTokenEntity;
import com.vacation.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.user = :user")
    int deleteByUser(@Param("user") UserEntity user);
}
