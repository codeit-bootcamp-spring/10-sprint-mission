package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> create(@RequestBody ChannelCreateRequest request)
            throws IOException, ClassNotFoundException {
        return ResponseEntity.status(201).body(channelService.create(request));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ChannelResponse> find(@PathVariable UUID id) throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(channelService.find(id));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findByUserId(@RequestParam UUID userId) throws IOException {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponse> update(@RequestBody PublicChannelUpdateRequest request)
            throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(channelService.update(request));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID id) throws IOException, ClassNotFoundException {
        channelService.delete(id);
        return ResponseEntity.status(204).body("Channel is removed");
    }
}
