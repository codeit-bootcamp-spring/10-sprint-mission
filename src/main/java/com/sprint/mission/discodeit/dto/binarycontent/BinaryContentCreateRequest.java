package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.utils.Validation;

// 유저 프로필 이미지, 메세지 첨부파일?
public record BinaryContentCreateRequest (
        String fileName,
        String contentType,
        byte[] bytes
) {
    public void validate(){
        Validation.notBlank(fileName, "파일 이름");
        Validation.notBlank(contentType, "콘텐츠 타입");
        if(bytes == null) throw new IllegalArgumentException("bytes는 비어 있을 수 없습니다.");
    }
}
