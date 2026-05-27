package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ChannelService {

    // Create
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    ChannelDto createPublic(PublicChannelCreateRequest publicChannelCreateRequest);

    ChannelDto createPrivate(PrivateChannelCreateRequest privateChannelCreateRequest);

    // Read
    ChannelDto findById(UUID id);

    // ReadAll
    List<ChannelDto> findAllByUserId(UUID userId);

    // Update
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    ChannelDto update(UUID id, PublicChannelUpdateRequest publicChannelUpdateRequest);

    // Delete
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    void delete(UUID id);
}
