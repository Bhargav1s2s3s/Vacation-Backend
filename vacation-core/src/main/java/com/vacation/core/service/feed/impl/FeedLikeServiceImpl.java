package com.vacation.core.service.feed.impl;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.common.error.exception.VacationException;
import com.vacation.core.entity.feed.FeedEntity;
import com.vacation.core.entity.feed.FeedLikeEntity;
import com.vacation.core.repository.feed.FeedLikeRepository;
import com.vacation.core.repository.feed.FeedRepository;
import com.vacation.core.service.feed.FeedLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FeedLikeServiceImpl implements FeedLikeService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public void likeFeed(UUID feedId, UUID profileId) {
        // Check if already liked
        if (feedLikeRepository.existsByFeedIdAndProfileId(feedId, profileId)) {
            throw new VacationException(VacationErrorCode.FEED_ALREADY_LIKED, "Feed already liked");
        }

        FeedEntity feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.FEED_NOT_FOUND, "Feed not found"));

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));

        // Insert like
        FeedLikeEntity like = new FeedLikeEntity();
        like.setFeed(feed);
        like.setProfile(profile);
        feedLikeRepository.save(like);

        // Increment like_count
        feedRepository.incrementLikeCount(feedId);
        log.info("Feed {} liked by profile {}", feedId, profileId);
    }

    @Transactional
    @Override
    public void unlikeFeed(UUID feedId, UUID profileId) {
        // Check if liked
        if (!feedLikeRepository.existsByFeedIdAndProfileId(feedId, profileId)) {
            throw new VacationException(VacationErrorCode.FEED_NOT_LIKED, "Feed not liked");
        }

        // Delete like
        feedLikeRepository.deleteByFeedIdAndProfileId(feedId, profileId);

        // Decrement like_count safely (never goes below 0)
        feedRepository.decrementLikeCount(feedId);
        log.info("Feed {} unliked by profile {}", feedId, profileId);
    }
}
