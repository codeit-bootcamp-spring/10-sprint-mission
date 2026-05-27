package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 채널 관리 Controller
 * 추후, 쿠키/세션 배우고 userId를 빼서 전달하게 수정?
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {
    private final ChannelService channelService;

    /**
     * 공개 채널 생성
     */
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> create(
            @RequestBody @Valid PublicChannelCreateRequest request
    ) {
        ChannelDto channel = channelService.createPublicChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    /**
     * 비공개 채널 생성
     */
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> create(
            @RequestBody @Valid PrivateChannelCreateRequest request
    ) {
        ChannelDto channel = channelService.createPrivateChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    /**
     * 특정 사용자가 볼 수 있는 모든 채널 목록
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @Schema(implementation = ChannelDto.class)
    public ResponseEntity<List<ChannelDto>> findAll(
            @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 공개 채널의 정보 수정
     */
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(examples = @ExampleObject("Private channel cannot be updated"))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject("Channel with id {channelId} not found")))
    })
    public ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
            @RequestBody @Valid PublicChannelUpdateRequest request) {
        ChannelDto channel = channelService.update(channelId, request);

        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    /**
     * 채널 삭제
     */
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    @Operation(summary = "Channel 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject("Channel with id {channelId} not found")))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
