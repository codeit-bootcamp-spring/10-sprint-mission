package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.IsMessageReadResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/channels/{channelId}")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/readstatus", method = RequestMethod.POST)
    public ReadStatusResponseDto createReadStatus(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ){
        return readStatusService.create(new ReadStatusCreateRequestDto(userId, channelId));
    }

    @RequestMapping(value = "/readstatus", method = RequestMethod.PATCH)
    public ReadStatusResponseDto updateReadStatus(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ){
        return readStatusService.update(new ReadStatusUpdateRequestDto(channelId, userId, Instant.now()));
    }

    @RequestMapping(value = "/messages/{messageId}/readstatus", method = RequestMethod.GET)
    public IsMessageReadResponseDto getReadStatus(
            @PathVariable UUID messageId,
            @RequestParam UUID userId
    ){
    return readStatusService.findByUserIdAndChannelId(userId, messageId);
    }

}
