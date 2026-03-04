package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "Public channel 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Channel created",
                    content = @Content(
                            schema = @Schema(implementation = Channel.class)
                    )
            )
    })
    @PostMapping("/public")
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);//201

    }

    @Operation(summary = "Private channel 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Channel created",
                    content = @Content(
                            schema = @Schema(implementation = Channel.class)
                    )
            )
    })
    @PostMapping("/private")
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> getChannel(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<Channel> updatePublicChannel(@PathVariable UUID channelId,
                                       @RequestBody PublicChannelUpdateRequest request){
        Channel channel = channelService.update(channelId, request);

        return ResponseEntity.ok(channel);

    }

    @Operation(summary = "channel 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel delete"
            )
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId){

        channelService.delete(channelId);
        return ResponseEntity.noContent().build();//204
    }


}
