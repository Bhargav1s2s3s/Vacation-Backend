package com.vacation.repository;

import com.vacation.entity.FeedImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImageEntity, UUID> {
}
