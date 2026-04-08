package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "binary_contents")
@Setter     // 매퍼가 못 찾아서 setter 설정
@NoArgsConstructor
public class BinaryContent extends BaseEntity {

    private String contentType;   // image/png, image/jpeg
    private String fileName;      // 원본 파일명
    private long size;            // 바이트 크기

    // 사용자 프로필 사진용
    public BinaryContent(String contentType, String fileName, long size) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                '}';
    }
}
