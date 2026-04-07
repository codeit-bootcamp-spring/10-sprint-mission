package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class FileStorageException extends StorageExcepton {

    public FileStorageException(Map<String, Object> details) {
        super(ErrorCode.FILE_STORAGE_ERROR, details);
    }
}
