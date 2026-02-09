package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.IsMessageReadResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/channels/{channelId}/readstatus", method = RequestMethod.POST)
    public ReadStatusResponseDto createReadStatus(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ){
        return readStatusService.create(new ReadStatusCreateRequestDto(userId, channelId));
    }

    @RequestMapping(value = "/channels/{channelId}/readstatus", method = RequestMethod.PATCH)
    public ReadStatusResponseDto updateReadStatus(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ){
        return readStatusService.update(new ReadStatusUpdateRequestDto(channelId, userId, Instant.now()));
    }

    @RequestMapping(value = "/channels/{channelId}/messages/{messageId}/readstatus", method = RequestMethod.GET)
    public IsMessageReadResponseDto getReadStatus(
            @PathVariable UUID messageId,
            @RequestParam UUID userId
    ){
        return readStatusService.findByUserIdAndMessageId(userId, messageId);
    }

    @RequestMapping(value = "/{userId}/readstatus")
    public List<IsMessageReadResponseDto> getAllReadStatus(@PathVariable UUID userId){
        return readStatusService.findAllByUserId(userId);
    }

}
