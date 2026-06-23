package com.vacation.core.repository.feed;

import com.vacation.core.entity.feed.FeedLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLikeEntity, UUID> {

    boolean existsByFeedIdAndProfileId(UUID feedId, UUID profileId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM FeedLikeEntity l WHERE l.feed.id = :feedId AND l.profile.id = :profileId")
    int deleteByFeedIdAndProfileId(@Param("feedId") UUID feedId, @Param("profileId") UUID profileId);
}
