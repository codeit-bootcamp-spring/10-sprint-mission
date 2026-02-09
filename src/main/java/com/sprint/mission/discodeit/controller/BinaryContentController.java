package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "{binaryContentId}", method = RequestMethod.GET)
    public BinaryContentResponseDTO getBinaryContent(@PathVariable UUID binaryContentId){

        return binaryContentService.find(binaryContentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentResponseDTO> getBinaryContents(@RequestParam List<UUID> ids){
        return binaryContentService.findAllByIdIn(ids);
    }

}
