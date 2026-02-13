package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PrivateCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.ChannelSummaryResponseDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChannelSummaryResponseDTO> create(@RequestBody PublicCreateRequestDTO publicCreateRequestDTO) {
        ChannelSummaryResponseDTO response = channelService.create(publicCreateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChannelSummaryResponseDTO> create(@RequestBody PrivateCreateRequestDTO privateCreateRequestDTO) {
        ChannelSummaryResponseDTO response = channelService.create(privateCreateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 공개 채널의 정보를 수정
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<ChannelSummaryResponseDTO> update(@PathVariable("channel-id") UUID channelId,
                                                            @RequestBody ChannelUpdateRequestDTO channelUpdateRequestDTO) {
        ChannelSummaryResponseDTO response = channelService.update(channelId, channelUpdateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 채널을 삭제
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록을 조회
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelDetailResponseDTO>> findAllByUserId(@RequestParam UUID userId) {
        List<ChannelDetailResponseDTO> response = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
