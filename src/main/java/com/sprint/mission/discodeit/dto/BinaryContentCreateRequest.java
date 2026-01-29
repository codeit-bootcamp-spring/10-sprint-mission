package com.sprint.mission.discodeit.dto;

// 유저 프로필 이미지, 메세지 첨부파일?
public record BinaryContentCreateRequest (
        String fileName,
        String contentType,
        byte[] bytes
) {

}
