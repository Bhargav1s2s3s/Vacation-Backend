package com.vacation.repository;

import com.vacation.entity.UserPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordResetEntity, UUID> {
}
