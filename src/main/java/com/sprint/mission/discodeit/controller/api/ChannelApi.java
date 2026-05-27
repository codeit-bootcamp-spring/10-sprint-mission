package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Public Channel 생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request);

  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Private Channel 생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request);

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Channel 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Channel 정보 수정 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Private Channel은 수정할 수 없음",
          content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))
      )
  })
  ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request);

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Channel 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID") UUID channelId
  );
}
