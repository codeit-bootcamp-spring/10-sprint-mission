package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.*;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;

    @PostMapping(value = "/public")
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody @Valid PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublic(request));
    }

    @PostMapping(value = "/private")
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody @Valid PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivate(request));
    }

    // deprecated ?
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ChannelResponse> find(@PathVariable UUID id) {
        return ResponseEntity.ok(channelService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findByUserId(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(channelService.findAllByUserId(userId));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<ChannelResponse> update(@PathVariable UUID id,
                                                  @RequestBody PublicChannelUpdateRequest request) {
        PublicChannelUpdateCommand command = new PublicChannelUpdateCommand(id, request.newName(), request.newDescription());
        return ResponseEntity.status(HttpStatus.OK).body(channelService.update(command));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.status(204).body("Channel is removed");
    }
}
