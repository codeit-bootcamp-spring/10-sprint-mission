package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
public class BinaryContent extends BaseEntity{

    @Column(name = "size")
    private long size;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

    public BinaryContent(long size, String fileName, String contentType){
        this.size = size;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public void updateStatus(BinaryContentStatus status){
        this.status = status;
    }
}
