package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Validated
@Tag(name = "Channel")
@Slf4j
public class ChannelController {

  private final ChannelService channelService;

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @PostMapping("/public")
  @Operation(summary = "Public Channel 생성")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  public ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request) {
    log.info("[CHANNEL] Public 채널 생성 요청: name={}", request.name());
    ChannelDto response = channelService.createPublic(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/private")
  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  public ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request) {
    log.info("[CHANNEL] Private 채널 생성 요청: participantIdCount={}", request.participantIds().size());
    ChannelDto response = channelService.createPrivate(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @PatchMapping("/{channelId}")
  @Operation(summary = "Channel 정보 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음"),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음")
  })
  public ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 Channel ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      @NotNull @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request
  ) {
    log.info("[CHANNEL] Public 채널 수정 요청: id={}", channelId);
    ChannelDto response = channelService.update(channelId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @DeleteMapping("/{channelId}")
  @Operation(summary = "Channel 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
  })
  public ResponseEntity<ChannelDto> delete(
      @Parameter(description = "삭제할 Channel ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      @NotNull @PathVariable UUID channelId
  ) {
    log.info("[CHANNEL] 채널 삭제 요청: id={}", channelId);
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
  public ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "조회할 User ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      @NotNull @RequestParam("userId") UUID userId
  ) {
    log.debug("[CHANNEL] 유저가 참여 중인 채널 목록 조회 요청: userId={}", userId);
    List<ChannelDto> responses = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
