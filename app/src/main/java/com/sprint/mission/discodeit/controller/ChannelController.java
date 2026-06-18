package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Channel")
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  // Public 채널 생성
  @Operation(summary = "Public Channel 생성")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @Valid @RequestBody ChannelDto.PublicChannelCreateRequest createReq) {
    log.info("[Controller] 공개채널 생성 요청: name={}, description={}",
        createReq.name(), createReq.description());

    ChannelDto dto = channelService.createChannel(createReq);
    log.debug("[Controller] 공개채널 생성 응답 준비: id={}, name={}", dto.id(),
        dto.name());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // Private 채널 생성
  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @Valid @RequestBody ChannelDto.PrivateChannelCreateRequest createReq) {
    log.info("[Controller] 비공개채널 생성 요청: participants={}",
        createReq.participantIds());

    ChannelDto dto = channelService.createChannel(createReq);
    log.debug("[Controller] 비공개채널 생성 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // 특정 사용자의 Public + Private 채널 조회
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
  @GetMapping(params = "userId")
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    List<ChannelDto> dto = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // Public 채널 수정
  @Operation(summary = "Channel 정보 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{channel-id}")
  public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable("channel-id") UUID channelId,
      @RequestBody ChannelDto.PublicChannelUpdateRequest updateReq) {
    log.info("[Controller] 채널 수정 요청: id={}, newName={}, newDescription={}",
        channelId, updateReq.newName(), updateReq.newDescription());

    ChannelDto dto = channelService.updateChannel(channelId, updateReq);
    log.debug("[Controller] 채널 수정 응답 준비: id={}", channelId);

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 채널 삭제
  @Operation(summary = "Channel 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{channel-id}")
  public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
    log.info("[Controller] 채널 삭제 요청: id={}", channelId);

    channelService.deleteChannel(channelId);
    log.debug("[Controller] 채널 삭제 응답 준비: id={}", channelId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
