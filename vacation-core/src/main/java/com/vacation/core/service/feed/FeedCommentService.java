package com.vacation.core.service.feed;

import com.vacation.core.dto.feed.CommentResponse;
import com.vacation.core.dto.feed.CreateCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedCommentService {

    CommentResponse addComment(UUID feedId, UUID profileId, CreateCommentRequest request);

    void deleteComment(UUID commentId, UUID profileId);

    Page<CommentResponse> getComments(UUID feedId, Pageable pageable);
}
