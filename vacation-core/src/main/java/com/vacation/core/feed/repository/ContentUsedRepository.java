package com.vacation.core.feed.repository;

import com.vacation.core.feed.entity.ContentUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentUsedRepository extends JpaRepository<ContentUsedEntity, UUID> {
}
