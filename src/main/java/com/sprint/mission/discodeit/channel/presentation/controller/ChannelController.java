package com.sprint.mission.discodeit.channel.presentation.controller;

import com.sprint.mission.discodeit.channel.application.service.ChannelService;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelApiMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final ChannelApiMapper mapper;

  @PostMapping(value = "/public")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody @Valid PublicChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(channelService.createPublic(request)));
  }

  @PostMapping(value = "/private")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(channelService.createPrivate(request)));
  }

  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findByUserId(@RequestParam UUID userId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.toDto(channelService.findAllByUserId(userId)));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID id,
      @RequestBody PublicChannelUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.toDto(channelService.update(id, request)));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    channelService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
