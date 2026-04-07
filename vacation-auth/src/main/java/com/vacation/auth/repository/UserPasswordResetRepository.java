package com.vacation.auth.repository;

import com.vacation.auth.entity.UserEntity;
import com.vacation.auth.entity.UserPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordResetEntity, UUID> {

    Optional<UserPasswordResetEntity> findByResetToken(String resetToken);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM UserPasswordResetEntity r WHERE r.user = :user")
    int deleteByUser(@Param("user") UserEntity user);
}
