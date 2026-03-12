package com.vacation.repository;

import com.vacation.entity.ContentUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentUsedRepository extends JpaRepository<ContentUsedEntity, UUID> {
}
