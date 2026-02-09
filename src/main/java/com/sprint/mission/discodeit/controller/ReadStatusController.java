package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<ReadStatusResponseDTO> getReadStatus(@PathVariable UUID userId){
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(value = "{channelId}", method = RequestMethod.POST)
    @ResponseBody
    public List<ReadStatusResponseDTO> createReadStatus(@PathVariable UUID channelId){
        return readStatusService.create(channelId);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public ReadStatusResponseDTO editReadStatus(@RequestBody ReadStatusUpdateRequestDTO req){
        return readStatusService.update(req);
    }


}
