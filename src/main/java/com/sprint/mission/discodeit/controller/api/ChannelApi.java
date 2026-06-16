package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 관리 API")
public interface ChannelApi {

    @Operation(summary = "공개 채널 생성")
    @ApiResponse(responseCode = "201", description = "성공적으로 생성됨")
    @PostMapping("/public")
    ResponseEntity<ChannelDto.Response> createPublicChannel(
            @RequestBody @Valid ChannelDto.PublicChannelCreateRequest request
    );

    @Operation(summary = "비공개 채널 생성")
    @ApiResponse(responseCode = "201", description = "성공적으로 생성됨")
    @PostMapping("/private")
    ResponseEntity<ChannelDto.Response> createPrivateChannel(
            @RequestBody @Valid ChannelDto.PrivateChannelCreateRequest request
    );

    @Operation(summary = "채널 정보 수정", description = "공개 채널의 이름과 설명을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "비공개 채널은 수정 불가"),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음")
    })
    @PatchMapping("/{channelId}")
    ResponseEntity<ChannelDto.Response> updateChannel(
            @Parameter(description = "수정할 채널 ID") @PathVariable("channelId") UUID channelId,
            @RequestBody @Valid ChannelDto.UpdatePublicRequest request
    );

    @Operation(summary = "채널 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{channelId}")
    ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 채널 ID") @PathVariable("channelId") UUID channelId
    );

    @Operation(summary = "채널 단건 조회")
    @GetMapping("/{channelId}")
    ResponseEntity<ChannelDto.Response> findChannel(
            @Parameter(description = "조회할 채널 ID") @PathVariable("channelId") UUID channelId
    );

    @Operation(summary = "사용자 참여 채널 목록 조회")
    @GetMapping
    ResponseEntity<List<ChannelDto.Response>> findAllByUser(
            @Parameter(description = "사용자 ID") @RequestParam("userId") UUID userId
    );

    @Operation(summary = "전체 채널 목록 조회", description = "시스템의 모든 채널을 조회합니다. (관리자 권한 필요)")
    @GetMapping("/findAll")
    ResponseEntity<List<ChannelDto.Response>> findAllChannels();
}
