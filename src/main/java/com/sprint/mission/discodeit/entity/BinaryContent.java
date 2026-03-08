package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = true)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

    public BinaryContent(String fileName, Long size, String contentType) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;

    }

    public BinaryContent() {

    }
}
