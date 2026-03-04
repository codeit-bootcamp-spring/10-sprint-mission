package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ApiResponse(
        responseCode = "201",
        description = "Public Channel이 성공적으로 생성됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChannelResponseDTO.class)
        )
    )
    public ResponseEntity<ChannelResponseDTO> createPublicChannel(
        @Valid @RequestBody PublicChannelCreateDTO req) {
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
            schema = @Schema(implementation = ChannelResponseDTO.class)
        )
    )
    public ResponseEntity<ChannelResponseDTO> createPrivateChannel(
        @RequestBody PrivateChannelCreateDTO req) {
        return new ResponseEntity<>(channelService.createPrivateChannel(req), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiResponse(
        responseCode = "200",
        description = "Channel 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = ChannelResponseDTO.class)
            )
        )
    )
    public ResponseEntity<List<ChannelResponseDTO>> getChannels(@RequestParam UUID userId) {
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
        channelService.delete(channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    @ApiResponse(
        responseCode = "200",
        description = "Channel 정보가 성공적으로 수정됨",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ChannelResponseDTO.class
            )
        )
    )
    public ResponseEntity<ChannelResponseDTO> updateChannel(@PathVariable UUID channelId,
        @RequestBody PublicChannelUpdateRequestDTO req) {
        return new ResponseEntity<>(channelService.update(channelId, req), HttpStatus.OK);
    }


}
