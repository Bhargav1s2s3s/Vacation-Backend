package com.vacation.core.shared.entity;

import com.vacation.common.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_media_type_ref", columnList = "media_type, reference_id")
})
@Data
@NoArgsConstructor
public class MediaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
