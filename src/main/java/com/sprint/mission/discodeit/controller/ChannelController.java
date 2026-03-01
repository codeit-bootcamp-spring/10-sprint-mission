package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

  private final ChannelService channelService;

  @RequestMapping(path = "/public", method = RequestMethod.POST)
  @Operation(summary = "Public Channel 생성")
  @ApiResponse(
          responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PublicChannelCreateRequest.class)
          )
  )
  public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @RequestMapping(path = "/private", method = RequestMethod.POST)
  @Operation(summary = "Private Channel 생성")
  @ApiResponse(
          responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PublicChannelCreateRequest.class)
          )
  )
  public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }


  @RequestMapping(path = "/{channelId}", method = RequestMethod.PATCH)
  @Operation(summary = "수정할 Channel ID")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
                        content = @Content(mediaType = "application.json",
                        schema = @Schema(implementation = Channel.class)
                        )
          ),
          @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Private channel cannot be updated")
                  )
          ),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Channel with id {channelId} not found")
                  )
          )
  })
  public ResponseEntity<Channel> updateChannel(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }


  @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
  @Operation(summary = "Channel 삭제")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Channel with id {channelId} not found")
                  )
          )
  })
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description ="Channel 목록 조회 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelDto.class)))
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
