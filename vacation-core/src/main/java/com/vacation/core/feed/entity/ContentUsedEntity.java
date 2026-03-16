package com.vacation.core.feed.entity;

import com.vacation.auth.profile.entity.ProfileEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "content_used")
@Data
@NoArgsConstructor
public class ContentUsedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(name = "data_id")
    private UUID dataId;
}
