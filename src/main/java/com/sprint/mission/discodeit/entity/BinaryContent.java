package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델로,
 * 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용 <br>
 * `User`, `Message` 도메인 모델과의 의존 관계 방향성 고려하여 `id` 참조 필드를 추가
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BinaryContentStatus status;

    // 생성자
    public BinaryContent(String fileName, String contentType, Long size) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.status = BinaryContentStatus.PROCESSING;
    }

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
    }
}
