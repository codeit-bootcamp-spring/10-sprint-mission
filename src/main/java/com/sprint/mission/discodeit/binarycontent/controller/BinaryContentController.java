package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binarycontents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public BinaryContentResponse find (@PathVariable UUID id){
        return binaryContentService.find(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentResponse> findAll (@RequestParam List<UUID> ids){
        return binaryContentService.findAllByIdIn(ids);
    }

    @RequestMapping(method = RequestMethod.POST)
    public BinaryContentResponse createBinaryContent(@RequestBody BinaryContentCreateRequest request){
        return binaryContentService.create(request);
    }
}
