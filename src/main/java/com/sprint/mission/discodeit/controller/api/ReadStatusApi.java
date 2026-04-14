package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

  @Operation(summary = "ReadStatus 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "ReadStatus 생성 성공",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "이미 ReadStatus가 존재함",
          content = @Content(examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))
      )
  })
  ResponseEntity<ReadStatusDto> create(
      @Parameter(description = "ReadStatus 생성 정보") ReadStatusCreateRequest request
  );

  @Operation(summary = "User의 ReadStatus 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "ReadStatus 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class)))
      )
  })
  ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID") @RequestParam UUID userId
  );

  @Operation(summary = "ReadStatus 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "ReadStatus 수정 성공",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "ReadStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found"))
      )
  })
  ResponseEntity<ReadStatusDto> update(
      @Parameter(description = "수정할 ReadStatus ID") UUID readStatusId,
      @Parameter(description = "수정할 ReadStatus 정보") ReadStatusUpdateRequest request
  );
}
