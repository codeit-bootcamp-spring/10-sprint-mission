package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
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
    private final ChannelMapper channelMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ReadStatusService readStatusService;

    @Operation(summary = "Public channel 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Channel created",
                    content = @Content(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            )
    })
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {

        ChannelDto channelDto = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);//201

    }

    @Operation(summary = "Private channel 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Channel created",
                    content = @Content(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            )
    })
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {

        ChannelDto channelDto = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> getChannel(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDto> getChannelById(@PathVariable("channelId") UUID channelId) {
        ChannelDto channelDto = channelService.find(channelId);
        return ResponseEntity.ok(channelDto);
    }


    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable UUID channelId,
                                       @RequestBody PublicChannelUpdateRequest request){
        ChannelDto channelDto = channelService.update(channelId, request);

        return ResponseEntity.ok(channelDto);

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
