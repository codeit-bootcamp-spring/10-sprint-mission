package com.sprint.mission.discodeit.binarycontent.mapper;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentInfo;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

public class BinaryContentMapper {
    private BinaryContentMapper(){}

    public static BinaryContentInfo toBinaryContentInfo(BinaryContent binaryContent) {
        return new BinaryContentInfo(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getContent()
        );
    }

    public static BinaryContent toBinaryContent(BinaryContentInfo binaryContentInfo) {
        return new BinaryContent(
                binaryContentInfo.content()
        );
    }
}
