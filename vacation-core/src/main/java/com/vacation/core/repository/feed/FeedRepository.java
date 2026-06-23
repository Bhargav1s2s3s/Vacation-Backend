package com.vacation.core.repository.feed;

import com.vacation.core.entity.feed.FeedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedRepository extends JpaRepository<FeedEntity, UUID> {

    @Query("SELECT f FROM FeedEntity f WHERE f.id = :feedId")
    Optional<FeedEntity> findByIdWithProfile(@Param("feedId") UUID feedId);

    Page<FeedEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<FeedEntity> findByProfileIdOrderByCreatedAtDesc(UUID profileId, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE FeedEntity f SET f.likeCount = f.likeCount + 1 WHERE f.id = :feedId")
    void incrementLikeCount(@Param("feedId") UUID feedId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE FeedEntity f SET f.likeCount = CASE WHEN f.likeCount > 0 THEN f.likeCount - 1 ELSE 0 END WHERE f.id = :feedId")
    void decrementLikeCount(@Param("feedId") UUID feedId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE FeedEntity f SET f.commentCount = f.commentCount + 1 WHERE f.id = :feedId")
    void incrementCommentCount(@Param("feedId") UUID feedId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE FeedEntity f SET f.commentCount = CASE WHEN f.commentCount > 0 THEN f.commentCount - 1 ELSE 0 END WHERE f.id = :feedId")
    void decrementCommentCount(@Param("feedId") UUID feedId);
}
