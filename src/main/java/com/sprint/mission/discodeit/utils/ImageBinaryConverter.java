package com.sprint.mission.discodeit.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageBinaryConverter {

    public static byte[] convert(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 변환을 실패하였습니다. multipartFile: " + multipartFile, e);
        }
    }
}
