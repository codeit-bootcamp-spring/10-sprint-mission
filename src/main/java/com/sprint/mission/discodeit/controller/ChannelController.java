package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @PostMapping(value = "/public")
    @Operation(summary = "Public Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Public Channel이 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            )
    })
    public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelDto response = channelService.create(publicChannelCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 비공개 채널 생성
    @PostMapping(value = "/private")
    @Operation(summary = "Private Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Private Channel이 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            )
    })
    public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelDto response = channelService.create(privateChannelCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 공개 채널의 정보를 수정
    @PatchMapping(value = "/{channelId}")
    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "PRIVATE 채널은 수정 불가능합니다")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "channelId: {channelId}를 가진 채널을 찾을 수 없습니다")
                    )
            )
    })
    public ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 Channel ID")
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelDto response = channelService.update(channelId, publicChannelUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 채널을 삭제
    @DeleteMapping(value = "/{channelId}")
    @Operation(summary = "Channel 삭제")
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
        )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Channel ID")
            @PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록을 조회
    @GetMapping
    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ChannelDto.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID")
            @RequestParam UUID userId) {
        List<ChannelDto> response = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
