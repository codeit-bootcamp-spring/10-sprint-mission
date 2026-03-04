package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContent extends BaseEntity {

    private byte[] bytes;
    private String contentType;   // image/png, image/jpeg
    private String fileName;      // 원본 파일명
    private long size;            // 바이트 크기

    // 사용자 프로필 사진용
    public BinaryContent(byte[] bytes, String contentType, String fileName) {
        super();

        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = bytes.length;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", bytes=" + bytes.length +
                ", contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                '}';
    }
}
