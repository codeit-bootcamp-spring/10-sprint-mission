package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ImageBinaryConverter {

    public static byte[] convert(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new ApiException(
                    ErrorCode.IMAGE_BINARY_CONVERSION_FAILED,
                    "이미지 파일 변환을 실패하였습니다. multipartFile: " + multipartFile
            );
        }
    }
}
