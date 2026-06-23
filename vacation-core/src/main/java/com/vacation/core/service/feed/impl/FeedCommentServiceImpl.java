package com.vacation.core.service.feed.impl;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.common.error.exception.VacationException;
import com.vacation.core.dto.feed.CommentResponse;
import com.vacation.core.dto.feed.CreateCommentRequest;
import com.vacation.core.entity.feed.FeedCommentEntity;
import com.vacation.core.entity.feed.FeedEntity;
import com.vacation.core.repository.feed.FeedCommentRepository;
import com.vacation.core.repository.feed.FeedRepository;
import com.vacation.core.service.feed.FeedCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FeedCommentServiceImpl implements FeedCommentService {

    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public CommentResponse addComment(UUID feedId, UUID profileId, CreateCommentRequest request) {
        FeedEntity feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.FEED_NOT_FOUND, "Feed not found"));

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));

        // Insert comment
        FeedCommentEntity comment = new FeedCommentEntity();
        comment.setFeed(feed);
        comment.setProfile(profile);
        comment.setCommentText(request.getComment());
        comment = feedCommentRepository.save(comment);

        // Increment comment_count
        feedRepository.incrementCommentCount(feedId);
        log.info("Comment added to feed {} by profile {}", feedId, profileId);

        return CommentResponse.builder()
                .commentId(comment.getId())
                .profileId(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .commentText(comment.getCommentText())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    @Override
    public void deleteComment(UUID commentId, UUID profileId) {
        FeedCommentEntity comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.COMMENT_NOT_FOUND, "Comment not found"));

        // Only the comment author can delete
        if (!comment.getProfile().getId().equals(profileId)) {
            throw new VacationException(VacationErrorCode.UNAUTHORIZED_ACTION, "You can only delete your own comments");
        }

        UUID feedId = comment.getFeed().getId();

        feedCommentRepository.delete(comment);

        // Decrement comment_count safely
        feedRepository.decrementCommentCount(feedId);
        log.info("Comment {} deleted from feed {} by profile {}", commentId, feedId, profileId);
    }

    @Override
    public Page<CommentResponse> getComments(UUID feedId, Pageable pageable) {
        return feedCommentRepository.findByFeedIdOrderByCreatedAtDesc(feedId, pageable)
                .map(comment -> {
                    ProfileEntity profile = comment.getProfile();
                    return CommentResponse.builder()
                            .commentId(comment.getId())
                            .profileId(profile.getId())
                            .firstName(profile.getFirstName())
                            .lastName(profile.getLastName())
                            .commentText(comment.getCommentText())
                            .createdAt(comment.getCreatedAt())
                            .build();
                });
    }
}
