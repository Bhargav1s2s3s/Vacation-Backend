package com.vacation.core.service.feed;

import java.util.UUID;

public interface FeedLikeService {

    void likeFeed(UUID feedId, UUID profileId);

    void unlikeFeed(UUID feedId, UUID profileId);
}
