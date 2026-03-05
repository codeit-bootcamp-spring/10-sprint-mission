package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수정 불가능한 도메인(updatedAt 필드 정의 X)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String fileName;
    @Column(nullable = false)
    private long size;
    @Column(nullable = false)
    private byte[] bytes;
    @Column(nullable = false, length = 100)
    private String contentType;

    public BinaryContent(String fileName, long size, byte[] bytes, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.bytes = bytes;
        this.contentType = contentType;
    }
}
