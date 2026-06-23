package com.vacation.core.service.feed.impl;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.common.enums.MediaType;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.core.dto.feed.*;
import com.vacation.core.entity.feed.FeedEntity;
import com.vacation.core.shared.dto.MediaDownloadInfo;
import com.vacation.core.shared.entity.MediaEntity;
import com.vacation.core.repository.feed.FeedLikeRepository;
import com.vacation.core.repository.feed.FeedRepository;
import com.vacation.core.repository.shared.MediaRepository;
import com.vacation.core.service.feed.FeedService;
import com.vacation.core.shared.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final MediaRepository mediaRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public CreateFeedResponse createFeed(UUID profileId, CreateFeedRequest request) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));

        // 1. Create feed
        FeedEntity feed = new FeedEntity();
        feed.setProfile(profile);
        feed.setCaption(request.getCaption());
        feed = feedRepository.save(feed);
        log.info("Feed created with id: {}", feed.getId());

        // 2. Batch create media rows with generated UUIDs and object_keys
        List<MediaEntity> mediaEntities = new ArrayList<>();
        for (int i = 0; i < request.getImageCount(); i++) {
            MediaEntity media = new MediaEntity();
            media.setId(UUID.randomUUID());
            media.setMediaType(MediaType.FEED);
            media.setReferenceId(feed.getId());
            media.setObjectKey("feeds/" + media.getId());
            mediaEntities.add(media);
        }
        mediaRepository.saveAll(mediaEntities);
        log.info("Batch saved {} feed media for feed: {}", mediaEntities.size(), feed.getId());

        // 3. Generate presigned upload URLs
        List<FeedMediaUploadInfo> uploadInfos = mediaEntities.stream()
                .map(m -> FeedMediaUploadInfo.builder()
                        .mediaId(m.getId())
                        .uploadUrl(fileStorageService.generatePresignedUploadUrl(m.getObjectKey()))
                        .build())
                .collect(Collectors.toList());

        return CreateFeedResponse.builder()
                .feedId(feed.getId())
                .images(uploadInfos)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public FeedDetailResponse getFeedDetail(UUID feedId, UUID profileId) {
        FeedEntity feed = feedRepository.findByIdWithProfile(feedId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.FEED_NOT_FOUND, "Feed not found"));

        ProfileEntity profile = feed.getProfile();
        boolean liked = profileId != null && feedLikeRepository.existsByFeedIdAndProfileId(feedId, profileId);

        List<MediaDownloadInfo> media = buildMediaDownloadList(MediaType.FEED, feedId);

        return FeedDetailResponse.builder()
                .feedId(feed.getId())
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .userImage(profile.getUserImage())
                .userImageUrl(resolveDownloadUrl(profile.getUserImage()))
                .caption(feed.getCaption())
                .likeCount(feed.getLikeCount())
                .commentCount(feed.getCommentCount())
                .liked(liked)
                .media(media)
                .createdAt(feed.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<FeedSummaryResponse> getFeeds(Pageable pageable) {
        return feedRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(feed -> {
                    ProfileEntity profile = feed.getProfile();
                    List<MediaDownloadInfo> media = buildMediaDownloadList(MediaType.FEED, feed.getId());

                    return FeedSummaryResponse.builder()
                            .feedId(feed.getId())
                            .profileId(profile.getId())
                            .firstName(profile.getFirstName())
                            .lastName(profile.getLastName())
                            .userImage(profile.getUserImage())
                            .userImageUrl(resolveDownloadUrl(profile.getUserImage()))
                            .caption(feed.getCaption())
                            .likeCount(feed.getLikeCount())
                            .commentCount(feed.getCommentCount())
                            .media(media)
                            .createdAt(feed.getCreatedAt())
                            .build();
                });
    }

    // ── helpers ──────────────────────────────────────────

    /**
     * Build a list of MediaDownloadInfo (mediaId + presigned download URL)
     * for all media of a given type and reference.
     */
    private List<MediaDownloadInfo> buildMediaDownloadList(MediaType mediaType, UUID referenceId) {
        return mediaRepository.findByMediaTypeAndReferenceId(mediaType, referenceId)
                .stream()
                .map(m -> MediaDownloadInfo.builder()
                        .mediaId(m.getId())
                        .downloadUrl(fileStorageService.generatePresignedDownloadUrl(m.getObjectKey()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Resolve a userImage (media ID stored as string) to a presigned download URL.
     * Returns null if the userImage is blank or the media record doesn't exist.
     */
    private String resolveDownloadUrl(String mediaIdStr) {
        if (mediaIdStr == null || mediaIdStr.isBlank()) {
            return null;
        }
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            return mediaRepository.findById(mediaId)
                    .map(m -> fileStorageService.generatePresignedDownloadUrl(m.getObjectKey()))
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
