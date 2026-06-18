package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserDto> participants,
    Instant lastMessageAt
) {

  public record PrivateChannelCreateRequest(
      @NotEmpty
      @Size(min = 2, message = "참여자가 최소 2명이어야 합니다")
      List<UUID> participantIds) {

  }

  public record PublicChannelCreateRequest(
      @NotBlank String name,
      String description) {

  }

  public record PublicChannelUpdateRequest(String newName, String newDescription) {

  }

  public record ChannelSummary(UUID id, ChannelType type, String name, String description,
                               Instant lastMessageAt) {

  }
}
