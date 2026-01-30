package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

@Getter
@ToString
public class BinaryContent extends BaseEntity{
    @Serial
    private static final long serialVersionUID = 1L;

    private final String fileName;
    private final byte[] data;

    public BinaryContent(String fileName, byte[] data) {
        super();
        this.fileName = fileName;
        this.data = data;
    }
}
