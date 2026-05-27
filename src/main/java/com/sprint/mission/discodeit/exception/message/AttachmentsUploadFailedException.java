package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class AttachmentsUploadFailedException extends MessageException {

    public AttachmentsUploadFailedException(UUID authorId, UUID channelId, Throwable e) {
        super(ErrorCode.ATTACHMENTS_UPLOAD_FAILED, Map.of("authorId", authorId, "channelId", channelId), e);
    }
}
