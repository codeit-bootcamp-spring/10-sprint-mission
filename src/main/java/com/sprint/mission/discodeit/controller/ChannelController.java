package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 채널 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * 모든 요청 매핑 및 Swagger 문서는 ChannelApi 인터페이스에 정의되어 있습니다.
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @Override
    public ResponseEntity<ChannelDto.Response> createPublicChannel(ChannelDto.PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
    }

    @Override
    public ResponseEntity<ChannelDto.Response> createPrivateChannel(ChannelDto.PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
    }

    @Override
    public ResponseEntity<ChannelDto.Response> updateChannel(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    @Override
    public ResponseEntity<Void> deleteChannel(UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ChannelDto.Response> findChannel(UUID channelId) {
        return ResponseEntity.ok(channelService.find(channelId));
    }

    @Override
    public ResponseEntity<List<ChannelDto.Response>> findAllByUser(UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @Override
    public ResponseEntity<List<ChannelDto.Response>> findAllChannels() {
        return ResponseEntity.ok(channelService.findAll());
    }
}
