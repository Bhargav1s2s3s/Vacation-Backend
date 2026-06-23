package com.vacation.core.repository.user;

import com.vacation.core.entity.user.ContentUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentUsedRepository extends JpaRepository<ContentUsedEntity, UUID> {
}
