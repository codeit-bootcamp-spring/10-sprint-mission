package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContent> find(@RequestParam UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<BinaryContent> findAll(@RequestParam List<UUID> Ids){
        return binaryContentService.findAllByIdIn(Ids);

    }
}
