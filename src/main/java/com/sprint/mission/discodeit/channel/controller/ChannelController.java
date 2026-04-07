package com.sprint.mission.discodeit.channel.controller;

import com.sprint.mission.discodeit.channel.dto.*;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성",
      operationId = "create_3")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201",
              description = "Public Channel이 성공적으로 생성됨"
          )
      }
  )
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @Parameter(description = "Public Channel 생성 정보")
      @Valid @RequestBody ChannelCreatePublicRequest request) {

    ChannelDto createdChannel = channelService.create(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Private Channel 생성",
      operationId = "create_4")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201",
          description = "Private Channel이 성공적으로 생성됨")
  })
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @Parameter(description = "Private Channel 생성 정보")
      @RequestBody ChannelCreatePrivateRequest request) {

    ChannelDto createdChannel = channelService.create(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Channel 정보 수정",
      operationId = "update_3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(
              schema = @Schema(implementation = ChannelResponse.class)
          )),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Private channel cannot be updated")
          )),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          ))
  }
  )
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> updatePublicChannel(
      @Parameter(description = "수정할 Channel ID")
      @PathVariable UUID channelId,
      @Parameter(description = "수정할 Channel 정보")
      @Valid @RequestBody ChannelUpdateRequest request) {

    ChannelDto updatedChannel = channelService.update(channelId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @Operation(summary = "Channel 삭제",
      operationId = "delete_2")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          )
      )}
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(
      @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId) {

    channelService.delete(channelId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }


  @Operation(summary = "User가 참여 중인 Channel 목록 조회",
      operationId = "findAll_1")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Channel 목록 조회 성공"
      )
  )
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUser(
      @Parameter(description = "조회할 User ID") @RequestParam UUID userId) {
    List<ChannelDto> channels = channelService.findByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }

}
