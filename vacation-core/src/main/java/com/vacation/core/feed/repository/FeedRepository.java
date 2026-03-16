package com.vacation.core.feed.repository;

import com.vacation.core.feed.entity.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedRepository extends JpaRepository<FeedEntity, UUID> {
}
