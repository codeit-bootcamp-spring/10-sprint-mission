package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContentResponseDto toDto(BinaryContent binaryContent){
        if(binaryContent == null)  return null;

        return new BinaryContentResponseDto(binaryContent.getId(),
                binaryContent.getUserId(),
                binaryContent.getMessageId(),
                binaryContent.getData(),
                binaryContent.getFileName(),
                binaryContent.getFileType());
    }
}
