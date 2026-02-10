package com.sprint.mission.discodeit.binarycontent.mapper;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContentResponse convertToResponse(BinaryContent binaryContent){
        return new BinaryContentResponse(
                binaryContent.getBytes(),
                binaryContent.getId()
        );
    }
}
