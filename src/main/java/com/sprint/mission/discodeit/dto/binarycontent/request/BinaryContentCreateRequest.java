package com.sprint.mission.discodeit.dto.binarycontent.request;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public record BinaryContentCreateRequest(@NotEmpty String fileName, byte[] data) {
    public static BinaryContentCreateRequest from(MultipartFile file) {
        try {
            return new BinaryContentCreateRequest(file.getOriginalFilename(), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
