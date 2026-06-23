package com.vacation.core.repository.shared;

import com.vacation.common.enums.MediaType;
import com.vacation.core.shared.entity.MediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {

    List<MediaEntity> findByMediaTypeAndReferenceId(MediaType mediaType, UUID referenceId);

    List<MediaEntity> findByMediaTypeAndReferenceIdIn(MediaType mediaType, Collection<UUID> referenceIds);

    void deleteByMediaTypeAndReferenceId(MediaType mediaType, UUID referenceId);
}
