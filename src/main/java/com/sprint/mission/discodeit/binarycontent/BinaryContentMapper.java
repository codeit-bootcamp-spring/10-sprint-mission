package com.sprint.mission.discodeit.binarycontent;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentInfo;

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
