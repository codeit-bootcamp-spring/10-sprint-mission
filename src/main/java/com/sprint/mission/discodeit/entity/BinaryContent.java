package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "BINARY_CONTENTS")
public class BinaryContent extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

//    @Column(nullable = false)
//    private byte[] bytes; //실제 바이너리 데이터는 별도의 공간에 저장하고, DB에는 파일명,크기,유형등만 저장.

    public BinaryContent(String fileName, Long size, String contentType) {
//        super();//id값과 createdAt값 주입

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
