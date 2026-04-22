package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.event.FileUploadResult;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID id, byte[] bytes);

  List<FileUploadResult> putAll(Map<UUID, byte[]> files);

  InputStream get(UUID id);

  ResponseEntity<?> download(BinaryContentDto dto);
}
