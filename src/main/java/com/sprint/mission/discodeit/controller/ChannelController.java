package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel")
public class ChannelController {

  private final ChannelService channelService;

  //공개 채널을 생성할 수 있다.
  @PostMapping("/public")
  @Operation(summary = "Public Channel 생성")
  public ResponseEntity<?> createPublic(@RequestBody ChannelDto.CreatePublic request) {
    ChannelDto.Response response = channelService.createPublic(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  //비공개 채널을 생성할 수 있다.
  @PostMapping("/private")
  @Operation(summary = "Private Channel 생성")
  public ResponseEntity<?> createPrivate(@RequestBody ChannelDto.CreatePrivate request) {
    ChannelDto.Response response = channelService.createPrivate(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  //공개 채널의 정보를 수정할 수 있다.
  @PatchMapping("/{channelId}")
  @Operation(summary = "Channel 정보 수정")
  public ResponseEntity<?> update(
      @PathVariable UUID channelId,
      @ModelAttribute ChannelDto.Update request
  ) {
    ChannelDto.Response response = channelService.update(channelId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  //채널을 삭제할 수 있다.
  @DeleteMapping("/{channelId}")
  @Operation(summary = "Channel 삭제")
  public ResponseEntity<?> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  //특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
  @GetMapping
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  public ResponseEntity<?> findAll(@RequestParam UUID userId) {
    List<ChannelDto.Response> responses = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
