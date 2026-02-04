package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class BinaryContent extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String fileName;
    private final String contentTYpe;
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentTYpe, byte[] bytes) {
        super();
        if (bytes == null) {
            throw new IllegalArgumentException("bytes는 null이 될 수 없습니다.");
        }
        this.fileName = fileName;
        this.contentTYpe = contentTYpe;
        this.bytes = bytes.clone();
    }
    public byte[] getBytes() {
        return bytes.clone();
    }
    public long getSize(){
        return bytes.length;
    }
}
