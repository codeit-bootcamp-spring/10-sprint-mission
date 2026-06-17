package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContentEntity extends BaseUpdatableEntity {
    @Column(nullable = false)
    private String fileName;                             // 파일 이름

    @Column(nullable = false)
    private Long size;                                   // 파일 크기

    @Column(nullable = false)
    private String contentType;                          // 파일 종류

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)                            // 첨부파일 상태
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

    public BinaryContentEntity(String originalFilename, long size, String contentType) {
        this.fileName = originalFilename;
        this.contentType = contentType;
        this.size = size;
    }

    public void updateStatus(BinaryContentStatus newStatus) {
        this.status = newStatus;
    }
}