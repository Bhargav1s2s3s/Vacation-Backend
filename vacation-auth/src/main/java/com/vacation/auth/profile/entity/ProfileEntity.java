package com.vacation.auth.profile.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.vacation.auth.entity.UserEntity;

import java.util.UUID;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @Column(name = "user_image")
    private String userImage;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Column(name = "last_name", length = 30)
    private String lastName;

    @Column(name = "email_id", length = 50, unique = true)
    private String emailId;

    @Column(name = "location", length = 100)
    private String location;
}
