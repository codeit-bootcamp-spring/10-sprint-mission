package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
public class ReadStatusController {
    private final ChannelService channelService;
    private final UserService userService;
    private final ReadStatusService readStatusService;

    public ReadStatusController(ChannelService channelService, UserService userService, ReadStatusService readStatusService) {
        this.channelService = channelService;
        this.userService = userService;
        this.readStatusService = readStatusService;
    }

    @RequestMapping(value = "/channel/readstatus", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> postReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return new ResponseEntity<>(readStatus, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/channel/readstatus/{readStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> putReadStatus(@PathVariable UUID readStatusId,
                              @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusService.update(readStatusId, request);
        return new ResponseEntity<>(readStatus, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/user/readStatus/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> getReadStatusAllByUserId(@PathVariable UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return new ResponseEntity<>(readStatuses, HttpStatus.OK);
    }
}
