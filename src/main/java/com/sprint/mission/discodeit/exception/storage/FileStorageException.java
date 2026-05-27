package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class FileStorageException extends StorageException {
    public FileStorageException(String fileName) {
        super(ErrorCode.FILE_STORAGE_ERROR, Map.of("fileName", fileName));
    }
}
