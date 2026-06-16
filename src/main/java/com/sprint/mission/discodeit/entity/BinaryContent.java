package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdateEntity {

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size", nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BinaryContentStatus status;

    public BinaryContent(String fileName, String contentType, long size, BinaryContentStatus status) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.status = status;
    }

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
    }
}
