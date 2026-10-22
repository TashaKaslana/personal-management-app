package com.example.personal_management_app.note.tag.infrastructure.persistence.repositories;

import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    // Find by name
    Optional<Tag> findByName(String name);

    // Check existence by name
    boolean existsByName(String name);

    // Check existence by name, excluding a given tag (used for renames)
    boolean existsByNameAndIdNot(String name, UUID id);

    // Bulk operations
    @Query("SELECT t FROM Tag t WHERE t.id IN :ids")
    List<Tag> findByIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    List<Tag> findByNameIn(@Param("names") List<String> names);
}
