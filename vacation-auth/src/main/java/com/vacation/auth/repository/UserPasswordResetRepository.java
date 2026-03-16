package com.vacation.auth.repository;

import com.vacation.auth.entity.UserPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordResetEntity, UUID> {
}
