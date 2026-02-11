package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentInfo;
import com.sprint.mission.discodeit.entity.BinaryContent;

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
