package com.sprint.mission.discodeit.global.infra.storage;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.common.storage.event.FileUploadResult;
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
