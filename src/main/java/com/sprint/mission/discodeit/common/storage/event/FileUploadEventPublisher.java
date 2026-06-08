package com.sprint.mission.discodeit.common.storage.event;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.entity.BaseEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUploadEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishAllFileUploadEvent(List<BinaryContent> entities,
      List<FileUploadRequest> files) {
    if (entities.isEmpty() || files.isEmpty()) {
      return;
    }
    applicationEventPublisher.publishEvent(new FileUploadEvent(zipIdWithBytes(entities, files)));
  }

  public void publishFileUploadEvent(BinaryContent entity, FileUploadRequest file) {
    Map<UUID, byte[]> data = Map.of(entity.getId(), file.bytes());
    applicationEventPublisher.publishEvent(new FileUploadEvent(data));
  }

  private <T extends BaseEntity> Map<UUID, byte[]> zipIdWithBytes(List<T> entities,
      List<FileUploadRequest> files) {
    return IntStream.range(0, entities.size())
        .boxed()
        .collect(Collectors.toMap(
            i -> entities.get(i).getId(),
            i -> files.get(i).bytes()));
  }
}
