package com.sprint.mission.discodeit.exception.s3;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class S3Exception extends DiscodeitException {

    public S3Exception() {
        super(ErrorCode.S3_FILE_UPLOAD_FAILED, Instant.now(), Map.of());
    }
}
