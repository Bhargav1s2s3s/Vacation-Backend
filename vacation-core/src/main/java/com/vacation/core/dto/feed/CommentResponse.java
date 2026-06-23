package com.vacation.core.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {
    private UUID commentId;
    private UUID profileId;
    private String firstName;
    private String lastName;
    private String commentText;
    private LocalDateTime createdAt;
}
