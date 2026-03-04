package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDocResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelDtoResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  public ChannelController(ChannelService channelService) {
    this.channelService = channelService;
  }

  // POST /api/channels/public -> 201 + Channel
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  })
  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelDocResponse> createPublic(
      @RequestBody PublicChannelCreateRequest dto
  ) {
    UUID id = channelService.createPublic(dto);
    Channel channel = channelService.findEntity(id);
    return ResponseEntity.status(201).body(toDocChannel(channel));
  }

  // POST /api/channels/private -> 201 + Channel
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  })
  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelDocResponse> createPrivate(
      @RequestBody PrivateChannelCreateRequest dto
  ) {
    UUID id = channelService.createPrivate(dto);
    Channel channel = channelService.findEntity(id);
    return ResponseEntity.status(201).body(toDocChannel(channel));
  }

  // GET /api/channels?userId= -> 200 + List<ChannelDto>
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDtoResponse>> findAllByUserId(
      @RequestParam UUID userId
  ) {
    return ResponseEntity.ok(
        channelService.findAllByUserId(userId).stream()
            .map(ChannelDtoResponse::from)
            .collect(Collectors.toList())
    );
  }

  // PATCH /api/channels/{channelId} -> 200 + Channel, 400 if private, 404
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Private channel cannot be updated"),
      @ApiResponse(responseCode = "404", description = "Channel with id {channelId} not found")
  })
  @RequestMapping(value = "/{channelId:[0-9a-fA-F\\-]{36}}", method = RequestMethod.PATCH)
  public ResponseEntity<?> update(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest dto) {

    try {
      ChannelUpdateRequest request =
          new ChannelUpdateRequest(channelId, dto.newName(), dto.newDescription());

      channelService.update(request);
      Channel channel = channelService.findEntity(channelId);

      return ResponseEntity.ok(toDocChannel(channel));
    } catch (IllegalStateException e) {
      // docs example: "Private channel cannot be updated"
      return ResponseEntity.badRequest().body("Private channel cannot be updated");
    }
  }

  // DELETE /api/channels/{channelId} -> 204, 404
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel with id {channelId} not found")
  })
  @RequestMapping(value = "/{channelId:[0-9a-fA-F\\-]{36}}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  private ChannelDocResponse toDocChannel(Channel c) {
    return ChannelDocResponse.of(
        c.getId(),
        c.getCreatedAt(),
        c.getUpdatedAt(),
        c.isPrivate(),
        c.getChannelName(),
        c.getDescription()
    );
  }
}