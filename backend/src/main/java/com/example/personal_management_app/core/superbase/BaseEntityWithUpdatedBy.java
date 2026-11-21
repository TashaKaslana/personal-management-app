package com.example.personal_management_app.core.superbase;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntityWithUpdatedBy extends BaseEntity{
    @Column(name = "updated_by")
    private UUID updatedBy;
}
