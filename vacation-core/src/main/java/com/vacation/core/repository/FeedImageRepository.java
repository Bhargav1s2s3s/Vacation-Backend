package com.vacation.core.repository;

import com.vacation.core.entity.FeedImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImageEntity, UUID> {
}
