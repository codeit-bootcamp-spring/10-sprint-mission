package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BinaryContentDto.Response findById(@PathVariable UUID id) {
        return binaryContentService.findById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentDto.Response> findAllIdIn(@RequestParam("ids") List<UUID> ids) {
        return binaryContentService.findAllIdIn(ids);
    }
}
