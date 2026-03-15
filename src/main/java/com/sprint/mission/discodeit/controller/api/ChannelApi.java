package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ChannelApi {

    ResponseEntity<ChannelDto> create(PublicChannelCreateRequest request);

    ResponseEntity<ChannelDto> create(PrivateChannelCreateRequest request);

    ResponseEntity<ChannelDto> update(UUID channelId, PublicChannelUpdateRequest request);

    ResponseEntity<Void> delete(UUID channelId);

    ResponseEntity<List<ChannelDto>> findAll(UUID userId);
}