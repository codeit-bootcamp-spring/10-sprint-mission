package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class BinaryContentMapper {
    public BinaryContentResponseDto toDto(BinaryContent content) {
        return new BinaryContentResponseDto(
                content.getId(),
                content.getFileName(),
                content.getContentType(),
                content.getBytes()
        );
    }
    public BinaryContent toEntity(BinaryContentCreateDto dto) {
        return new BinaryContent(
                dto.fileName(),
                dto.contentType(),
                dto.bytes()
        );
    }
    public BinaryContentCreateDto toCreateDto(MultipartFile multipartFile) {
        try {
            return new BinaryContentCreateDto(multipartFile.getName(),
                    multipartFile.getContentType(),
                    multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}