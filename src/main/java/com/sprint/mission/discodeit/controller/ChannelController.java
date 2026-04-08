package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.UpdateChannelRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Channel")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    @RequestMapping(method = RequestMethod.POST, value = "/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublic(@Valid @RequestBody CreatePublicChannelRequestDto request){
        log.info("REST 요청 - Public Channel 생성 시작: name={}", request.name());
        return channelService.createPublic(request);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    @RequestMapping(method = RequestMethod.POST, value = "/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivate(@Valid @RequestBody CreatePrivateChannelRequestDto request){
        log.info("REST 요청 - Private Channel 생성 시작");
        return channelService.createPrivate(request);
    }

    // 이미 방어 로직 구현되어있음
    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
            @Valid @RequestBody UpdateChannelRequestDto request){
        log.info("REST 요청 - Channel 정보 수정 시작: channelId={}", channelId);
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId){
        log.warn("REST 요청 - Channel 삭제 시작: channelId={}", channelId);
        channelService.delete(channelId);
    }


    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@Parameter(description = "조회할 User ID")
                                                              @RequestParam UUID userId)
    {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
