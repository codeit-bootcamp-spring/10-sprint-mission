package com.sprint.mission.discodeit.storage;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

public interface BinaryContentStorage {

	UUID put(UUID binaryContentId, byte[] bytes);

	InputStream get(UUID binaryContentId);

	ResponseEntity<?> download(BinaryContentDto metaData);
}
