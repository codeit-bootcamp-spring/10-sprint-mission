package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BinaryContent extends BaseEntity {
    //
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
//        super();//id값과 createdAt값 주입

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
