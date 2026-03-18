package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  @PostMapping(path = "/public")
  public ResponseEntity<ChannelDto> create(
      @Parameter(description = "Public Channel 생성 정보") @Valid @RequestBody PublicChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  @PostMapping(path = "/private")
  public ResponseEntity<ChannelDto> create(
      @Parameter(description = "Private Channel 생성 정보") @Valid @RequestBody PrivateChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED\", "
                      + "\"message\": \"비공개 채널은 수정할 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"CHANNEL_NOT_FOUND\", "
                      + "\"message\": \"채널을 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PatchMapping(path = "/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
      @Parameter(description = "수정할 Channel 정보") @Valid @RequestBody PublicChannelUpdateRequest request) {
    ChannelDto updatedChannel = channelService.update(channelId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"CHANNEL_NOT_FOUND\", "
                      + "\"message\": \"채널을 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @DeleteMapping(path = "/{channelId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
