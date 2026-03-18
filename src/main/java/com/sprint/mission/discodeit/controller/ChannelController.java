package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelDocResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;
  private final UserService userService;
  private final BinaryContentService binaryContentService;

  public ChannelController(
      ChannelService channelService,
      UserService userService,
      BinaryContentService binaryContentService
  ) {
    this.channelService = channelService;
    this.userService = userService;
    this.binaryContentService = binaryContentService;
  }

  @Operation(summary = "Public Channel 생성", operationId = "create_3", tags = {"Channel"})
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  })
  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest dto) {
    UUID id = channelService.createPublic(dto);
    return ResponseEntity.status(201).body(toDto(channelService.find(id)));
  }

  @Operation(summary = "Private Channel 생성", operationId = "create_4", tags = {"Channel"})
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  })
  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest dto) {
    UUID id = channelService.createPrivate(dto);
    return ResponseEntity.status(201).body(toDto(channelService.find(id)));
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1", tags = {"Channel"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID")
      @RequestParam UUID userId
  ) {
    return ResponseEntity.ok(
        channelService.findAllByUserId(userId).stream()
            .map(this::toDto)
            .toList()
    );
  }

  @Operation(summary = "Channel 정보 수정", operationId = "update_3", tags = {"Channel"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "400",
          description = "Private Channel은 수정할 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Private channel cannot be updated")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          )
      )
  })
  @RequestMapping(value = "/{channelId:[0-9a-fA-F\\-]{36}}", method = RequestMethod.PATCH)
  public ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 Channel ID")
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest dto
  ) {
    ChannelUpdateRequest request =
        new ChannelUpdateRequest(channelId, dto.newName(), dto.newDescription());

    return ResponseEntity.ok(toDto(channelService.update(request)));
  }

  @Operation(summary = "Channel 삭제", operationId = "delete_2", tags = {"Channel"})
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          )
      )
  })
  @RequestMapping(value = "/{channelId:[0-9a-fA-F\\-]{36}}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID")
      @PathVariable UUID channelId
  ) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  private ChannelDto toDto(ChannelResponse response) {
    List<UserDto> participants = response.participantIds() == null
        ? List.of()
        : response.participantIds().stream()
            .map(userService::find)
            .map(this::toUserDto)
            .toList();

    return new ChannelDto(
        response.channelId(),
        response.isPrivate() ? ChannelDocResponse.ChannelType.PRIVATE
            : ChannelDocResponse.ChannelType.PUBLIC,
        response.channelName(),
        response.description(),
        participants,
        response.lastMessageTime()
    );
  }

  private UserDto toUserDto(UserResponse userResponse) {
    BinaryContentDto profile = null;
    if (userResponse.profileImageId() != null) {
      BinaryContentResponse binary = binaryContentService.find(userResponse.profileImageId());
      profile = new BinaryContentDto(
          binary.id(),
          binary.fileName(),
          binary.size(),
          binary.contentType()
      );
    }

    return new UserDto(
        userResponse.id(),
        userResponse.userName(),
        userResponse.email(),
        profile,
        userResponse.online()
    );
  }
}