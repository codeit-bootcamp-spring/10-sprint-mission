package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping("public")
  @Override
  public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(channelService.create(request));
  }

  @PostMapping("private")
  @Override
  public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(channelService.create(request));
  }

  @PatchMapping("{channelId}")
  @Override
  public ResponseEntity<ChannelDto> update(
          @PathVariable("channelId") UUID channelId,
          @RequestBody PublicChannelUpdateRequest request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelService.update(channelId, request));
  }

  @DeleteMapping("{channelId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelService.findAllByUserId(userId));
  }
}