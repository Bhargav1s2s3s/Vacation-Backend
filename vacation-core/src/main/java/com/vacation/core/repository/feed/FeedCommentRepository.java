package com.vacation.core.repository.feed;

import com.vacation.core.entity.feed.FeedCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedCommentEntity, UUID> {

    Page<FeedCommentEntity> findByFeedIdOrderByCreatedAtDesc(UUID feedId, Pageable pageable);
}
