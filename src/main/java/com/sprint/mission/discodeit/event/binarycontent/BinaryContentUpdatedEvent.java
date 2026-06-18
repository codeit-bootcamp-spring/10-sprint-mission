package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentUpdatedEvent {

  private final BinaryContentDto binaryContent;
}
