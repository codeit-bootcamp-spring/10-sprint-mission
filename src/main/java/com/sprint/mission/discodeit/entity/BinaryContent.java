package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
public class BinaryContent extends BaseUpdatableEntity {

    private String contentType;   // image/png, image/jpeg
    private String fileName;      // 원본 파일명
    private long size;            // 바이트 크기

    @Enumerated(EnumType.STRING)
    private BinaryContentStatus status;     // 상태값

    // 사용자 프로필 사진용
    public BinaryContent(String contentType, String fileName, long size) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
        this.status = BinaryContentStatus.PROCESSING;
    }

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
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
