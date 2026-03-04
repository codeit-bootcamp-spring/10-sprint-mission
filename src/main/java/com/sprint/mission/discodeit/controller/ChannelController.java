package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "Channel")
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    // Public 채널 생성
    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.channelResponse> createPublicChannel(@RequestBody ChannelDto.channelCreatePublicRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.createChannel(createReq));
    }

    // Private 채널 생성
    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.channelResponse> createPrivateChannel(@RequestBody ChannelDto.channelCreatePrivateRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.createChannel(createReq));
    }

//    // 채널 단일 조회(UUID)
//    @RequestMapping(value = "/{channel-id}", method = RequestMethod.GET)
//    public ResponseEntity<ChannelDto.channelResponse> findChannel(@PathVariable("channel-id") UUID channelId) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(channelService.findChannel(channelId));
//    }
//
//    // 채널 단일 조회(Title)
//    @RequestMapping(params = "title", method = RequestMethod.GET)
//    public ResponseEntity<ChannelDto.channelResponse> findChannelByTitle(@RequestParam String title) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(channelService.findChannelByTitle(title));
//    }

    // 특정 사용자의 Public + Private 채널 조회
    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @RequestMapping(params = "userId", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto.channelResponse>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(channelService.findAllByUserId(userId));
    }

    // Public 채널 수정
    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto.channelResponse> updatePublicChannel(@PathVariable("channel-id") UUID channelId,
                                                                          @RequestBody ChannelDto.channelUpdatePublicRequest updateReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(channelService.updateChannel(channelId, updateReq));
    }

    // 채널 삭제
    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    // 채널 참가
//    @RequestMapping(value = "/{channel-id}/join/{user-id}", method = RequestMethod.PATCH)
//    public ResponseEntity<Void> joinChannel(@PathVariable("channel-id") UUID channelId,
//                                            @PathVariable("user-id") UUID userId) {
//        channelService.joinChannel(channelId, userId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    // 채널 퇴장
//    @RequestMapping(value = "/{channel-id}/leave/{user-id}", method = RequestMethod.PATCH)
//    public ResponseEntity<Void> leaveChannel(@PathVariable("channel-id") UUID channelId,
//                                             @PathVariable("user-id") UUID userId) {
//        channelService.leaveChannel(channelId, userId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
