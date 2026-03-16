package com.vacation.core.feed.repository;

import com.vacation.core.feed.entity.FeedImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImageEntity, UUID> {
}
