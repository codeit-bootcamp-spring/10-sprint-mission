package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class ProfileImageData {
    private String contentType;
    private byte[] data;

    public ProfileImageData(String contentType, byte[] data) {
        this.contentType = contentType;
        this.data = data;
    }

//    // Getters
//    public String getContentType() { return contentType; }
//    public byte[] getData() { return data; }
}
