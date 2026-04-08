package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.exception.binarycontent.ImageBinaryConversionException;
import java.io.IOException;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public class ImageBinaryConverter {

    public static byte[] convert(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            Map<String, Object> details = new java.util.LinkedHashMap<>();
            if (multipartFile.getOriginalFilename() != null) {
                details.put("fileName", multipartFile.getOriginalFilename());
            }
            if (multipartFile.getContentType() != null) {
                details.put("contentType", multipartFile.getContentType());
            }
            throw new ImageBinaryConversionException(
                    "이미지 파일 변환을 실패하였습니다. multipartFile: " + multipartFile,
                    details,
                    e
            );
        }
    }
}
