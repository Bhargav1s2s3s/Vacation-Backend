package com.vacation.repository;

import com.vacation.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    @Modifying
    @Query("UPDATE ProfileEntity p SET p.emailId = :email WHERE p.user.id = :id")
    void setEmailIdInProfile(@Param("id") UUID id, @Param("email") String email);

    boolean existsByEmailId(String email);
}
