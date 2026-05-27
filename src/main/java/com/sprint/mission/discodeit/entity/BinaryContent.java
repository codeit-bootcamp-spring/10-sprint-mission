package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
* 수정 불가능한 도메인 모델
* */
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class BinaryContent extends BaseEntity {
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private String contentType;
    public BinaryContent(String fileName, String contentType, long size) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }
}
