package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel controller 입니다.")
public class ChannelController {

  private final ChannelService channelService;

  @RequestMapping(value = "/public", method = RequestMethod.POST)
  @Operation(summary = "Public Channel 생성", operationId = "create_3")
  public ResponseEntity<ChannelResponseDto> createPublicChannel(
      @Valid @RequestBody PublicChannelPostDto publicChannelPostDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublicChannel(publicChannelPostDto));
  }

  @RequestMapping(value = "/private", method = RequestMethod.POST)
  @Operation(summary = "Private Channel 생성", operationId = "create_4")
  public ResponseEntity<ChannelResponseDto> createPrivateChannel(
      @RequestBody PrivateChannelPostDto privateChannelPostDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivateChannel(privateChannelPostDto));
  }

  @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
  @Operation(summary = "Channel 정보 수정", operationId = "update_3")
  public ResponseEntity<ChannelResponseDto> updatePublicChannel(
      @Parameter(name = "channelId", description = "수정할 Channel ID") @PathVariable UUID channelId,
      @Valid @RequestBody ChannelPatchDto channelPatchDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(channelService.update(channelId, channelPatchDto));
  }

  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  @Operation(summary = "Channel 삭제", operationId = "delete_2")
  public ResponseEntity<ChannelResponseDto> updatePublicChannel(
      @Parameter(name = "channelId", description = "삭제할 Channel ID") @PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
  public ResponseEntity<List<ChannelResponseDto>> getChannelsByUserId(
      @Parameter(name = "userId", description = "조회할 User ID") @RequestParam(value = "userId") UUID userId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(channelService.findAllByUserId(userId));
  }
}
