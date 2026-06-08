package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;


    @GetMapping(value = "/all")
    public ResponseEntity<List<ChannelDto>> findAllChannel() {
        log.trace("[Channel] 컨트롤러에서 전체 목록 조회 요청 받음");
        return new ResponseEntity<>(channelService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ApiResponse(
        responseCode = "201",
        description = "Public Channel이 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelDto.class)
        )
    )
    public ResponseEntity<ChannelDto> createPublicChannel(
        @Valid @RequestBody PublicChannelCreateDTO req) {
        log.trace("[Channel] 컨트롤러에서 공개 채널 생성 요청 받음");
        return new ResponseEntity<>(channelService.createPublicChannel(req), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseBody
    @ApiResponse(
        responseCode = "201",
        description = "Private Channel이 성공적으로 생성됨",
        content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelDto.class)
        )
    )
    public ResponseEntity<ChannelDto> createPrivateChannel(
        @Valid @RequestBody PrivateChannelCreateDTO req) {
        return new ResponseEntity<>(channelService.createPrivateChannel(req), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiResponse(
        responseCode = "200",
        description = "Channel 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = ChannelDto.class)
            )
        )
    )
    public ResponseEntity<List<ChannelDto>> getChannels(@RequestParam UUID userId) {
        log.trace("[Channel] 컨트롤러에서 채널 목록 조회 요청 받음");
        return new ResponseEntity<>(channelService.findAllByUserId(userId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "Channel을 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Channel with id {channelId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "204",
            description = "Channel이 성공적으로 삭제됨"
        )
    })
    public void deleteChannel(@PathVariable UUID channelId) {
        ChannelDto channel = channelService.find(channelId);
        switch (channel.type()) {
            case PUBLIC -> channelService.deletePublicChannel(channelId);
            case PRIVATE -> channelService.deletePrivateChannel(channelId);
        }
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    @ApiResponse(
        responseCode = "200",
        description = "Channel 정보가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ChannelDto.class
            )
        )
    )
    public ResponseEntity<ChannelDto> updateChannel(@PathVariable UUID channelId,
        @Valid @RequestBody PublicChannelUpdateRequestDTO req) {
        log.trace("[Channel] 컨트롤러에서 채널 업데이트 요청 받음");
        ChannelDto channel = channelService.find(channelId);
        ChannelDto updated = switch (channel.type()) {
            case PUBLIC -> channelService.updatePublicChannel(channelId, req);
            case PRIVATE -> channelService.updatePrivateChannel(channelId, req);
        };
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


}
