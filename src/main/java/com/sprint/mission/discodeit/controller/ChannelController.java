package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  // POST /api/channels/public
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublic(
      @RequestBody PublicChannelCreateRequest request) {
    Channel created = channelService.create(request);
    ChannelDto dto = channelService.find(created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(ChannelResponse.from(dto));
  }

  // POST /api/channels/private
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    Channel created = channelService.create(request);
    ChannelDto dto = channelService.find(created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(ChannelResponse.from(dto));
  }

  // PATCH /api/channels/{channelId}
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request
  ) {
    channelService.update(channelId, request);
    ChannelDto dto = channelService.find(channelId);
    return ResponseEntity.ok(ChannelResponse.from(dto));
  }

  // DELETE /api/channels/{channelId}
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  // GET /api/channels/{channelId}
  @GetMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> find(@PathVariable UUID channelId) {
    ChannelDto dto = channelService.find(channelId);
    return ResponseEntity.ok(ChannelResponse.from(dto));
  }

  // [Requirement] Channel that specific user can view
  // GET  /api/channels?userId={userId}
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    List<ChannelResponse> responses = channels.stream()
        .map(ChannelResponse::from)
        .toList();
    return ResponseEntity.ok(responses);
  }
}
