package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
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

  @PostMapping(value = "/public")
  public ResponseEntity<ChannelDto> create(@RequestBody @Valid PublicChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublic(request));
  }

  @PostMapping(value = "/private")
  public ResponseEntity<ChannelDto> create(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivate(request));
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findByUserId(@RequestParam UUID userId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(channelService.findAllByUserId(userId));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID id,
      @RequestBody PublicChannelUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(channelService.update(id, request));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    channelService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
