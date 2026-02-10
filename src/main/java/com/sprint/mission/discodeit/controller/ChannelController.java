package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDTO> createPublicChannel(@RequestBody PublicChannelCreateRequestDTO publicChannelCreateRequestDTO) {
        ChannelResponseDTO newChannel = channelService.createPublicChannel(publicChannelCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    // 비공개 채널 생성
    @RequestMapping(value = "private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDTO> createPrivateChannel(@RequestBody PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO) {
        ChannelResponseDTO newChannel = channelService.createPrivateChannel(privateChannelCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록 조회
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDTO>> findAllByUserId(@PathVariable UUID userId) {
        List<ChannelResponseDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }

    // 공개 채널 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ChannelResponseDTO> update(@PathVariable UUID id,
                                                     @RequestBody ChannelUpdateRequestDTO channelUpdateRequestDTO) {

        ChannelResponseDTO updateChannel = channelService.update(id, channelUpdateRequestDTO);

        return ResponseEntity.ok(updateChannel);
    }

    // 채널 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ChannelResponseDTO> delete(@PathVariable UUID id) {
        channelService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
