package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class AwsServerConnectFailedException extends BinaryContentException {

    public AwsServerConnectFailedException(UUID binaryContentId, Throwable e) {
        super(ErrorCode.AWS_SERVER_CONNECT_FAILED, "binaryContentId", binaryContentId, e);
    }
}
