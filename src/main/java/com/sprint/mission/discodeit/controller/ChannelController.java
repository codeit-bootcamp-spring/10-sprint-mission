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

@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> createPublicChannel(
            @Valid @RequestBody PublicChannelCreateRequest request
    ) {
        log.info("Public 채널 생성 요청: name={}", request.name());
        ChannelDto response = channelService.createPublicChannel(request);
        log.info("Public 채널 생성 완료: channelId={}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/private")
    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> createPrivateChannel(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ) {
        log.info("Private 채널 생성 요청: participantIds={}", request.participantIds());
        ChannelDto response = channelService.createPrivateChannel(request);
        log.info("Private 채널 생성 완료: channelId={}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{channelId}")
    @Operation(summary = "Channel 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 조회 성공"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
    })
    public ResponseEntity<ChannelDto> findChannelByChannelId(
            @Parameter(description = "조회할 Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
            @PathVariable UUID channelId
    ) {
        log.debug("채널 단건 조회 요청: channelId={}", channelId);
        ChannelDto response = channelService.findChannelByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    public ResponseEntity<List<ChannelDto>> findAllChannelsByUserId(
            @Parameter(description = "조회할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @RequestParam UUID userId
    ) {
        log.debug("사용자별 채널 목록 조회 요청: userId={}", userId);
        List<ChannelDto> responses = channelService.findAllChannelsByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{channelId}")
    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음")
    })
    public ResponseEntity<ChannelDto> updateChannelInfo(
            @Parameter(description = "수정할 Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request
    ) {
        log.info("채널 수정 요청: channelId={}", channelId);
        ChannelDto response = channelService.updateChannelInfo(channelId, request);
        log.info("채널 수정 완료: channelId={}", channelId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{channelId}")
    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
            @PathVariable UUID channelId
    ) {
        log.info("채널 삭제 요청: channelId={}", channelId);
        channelService.deleteChannel(channelId);
        log.info("채널 삭제 완료: channelId={}", channelId);

        return ResponseEntity.noContent().build();
    }
}
