package com.vacation.core.controller.feed;

import com.vacation.common.config.BaseController;
import com.vacation.core.dto.feed.*;
import com.vacation.core.service.feed.FeedCommentService;
import com.vacation.core.service.feed.FeedLikeService;
import com.vacation.core.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class FeedController extends BaseController {

    private final FeedService feedService;
    private final FeedLikeService feedLikeService;
    private final FeedCommentService feedCommentService;

    // ======================== FEED CRUD ========================

    @PostMapping("/core/feeds")
    public ResponseEntity<CreateFeedResponse> createFeed(
            @RequestParam UUID profileId,
            @RequestBody CreateFeedRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(feedService.createFeed(profileId, request));
    }

    @GetMapping("/core/feeds/{feedId}")
    public ResponseEntity<FeedDetailResponse> getFeedDetail(
            @PathVariable UUID feedId,
            @RequestParam(required = false) UUID profileId) {
        return ResponseEntity.ok(feedService.getFeedDetail(feedId, profileId));
    }

    @GetMapping("/core/feeds")
    public ResponseEntity<Page<FeedSummaryResponse>> getFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(feedService.getFeeds(PageRequest.of(page, size)));
    }

    // ======================== LIKES ========================

    @PostMapping("/core/feeds/{feedId}/likes")
    public ResponseEntity<Void> likeFeed(
            @PathVariable UUID feedId,
            @RequestParam UUID profileId) {
        feedLikeService.likeFeed(feedId, profileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/core/feeds/{feedId}/likes")
    public ResponseEntity<Void> unlikeFeed(
            @PathVariable UUID feedId,
            @RequestParam UUID profileId) {
        feedLikeService.unlikeFeed(feedId, profileId);
        return ResponseEntity.noContent().build();
    }

    // ======================== COMMENTS ========================

    @PostMapping("/core/feeds/{feedId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID feedId,
            @RequestParam UUID profileId,
            @RequestBody CreateCommentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(feedCommentService.addComment(feedId, profileId, request));
    }

    @DeleteMapping("/core/feeds/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID profileId) {
        feedCommentService.deleteComment(commentId, profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/core/feeds/{feedId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable UUID feedId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(feedCommentService.getComments(feedId, PageRequest.of(page, size)));
    }
}
