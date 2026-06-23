package com.vacation.core.entity.feed;

import com.vacation.auth.entity.ProfileEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feed_likes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_feed_like", columnNames = {"feed_id", "profile_id"})
})
@Data
@NoArgsConstructor
public class FeedLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private FeedEntity feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
