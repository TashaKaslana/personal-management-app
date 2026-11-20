package com.example.personal_management_app.note.tag.infrastructure.persistence.repositories;

import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    // Find by name
    Optional<Tag> findByNameAndOwnerId(String name, UUID ownerId);

    Optional<Tag> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Tag> findAllByOwnerId(UUID ownerId);

    Page<Tag> findAllByOwnerId(UUID ownerId, Pageable pageable);

    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

    long countByOwnerId(UUID ownerId);

    // Check existence by name
    boolean existsByNameAndOwnerId(String name, UUID ownerId);

    // Check existence by name, excluding a given tag (used for renames)
    boolean existsByNameAndOwnerIdAndIdNot(String name, UUID ownerId, UUID id);

    // Bulk operations
    @Query("SELECT t FROM Tag t WHERE t.id IN :ids AND t.ownerId = :ownerId")
    List<Tag> findByIdInAndOwnerId(@Param("ids") List<UUID> ids, @Param("ownerId") UUID ownerId);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names AND t.ownerId = :ownerId")
    List<Tag> findByNameInAndOwnerId(@Param("names") List<String> names, @Param("ownerId") UUID ownerId);
}
