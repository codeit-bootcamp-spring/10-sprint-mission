package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // binary-Content 단건 조회
    @RequestMapping(value="/{content-id}", method= RequestMethod.GET)
    public BinaryContentResponse getBinaryContent(@PathVariable("content-id") UUID contentID){
        return binaryContentService.find(contentID);
    }

    // binary-conent 다건 조회
    @RequestMapping(method=RequestMethod.GET)
    public List<BinaryContentResponse> getBinaryContents(@RequestParam("ids") List<UUID> contentIDs){
        return binaryContentService.findAllByIdIn(contentIDs);
    }

}
