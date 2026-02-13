package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusInfoDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // 특정 채널 메시지 수신 정보 생성
  @RequestMapping(method = RequestMethod.POST)
  public ReadStatusInfoDto create(@RequestBody ReadStatusCreateDto dto) {
    return readStatusService.create(dto);
  }

  // 특정 채널 메시지 수신 정보 수정
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
  public ReadStatusInfoDto update(@PathVariable UUID id, @RequestBody ReadStatusUpdateDto dto) {
    return readStatusService.update(id, dto);
  }

  // 특정 사용자의 메시지 수신 정보 조회
  @RequestMapping(method = RequestMethod.GET)
  public List<ReadStatusInfoDto> findAllByUserId(@RequestParam UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }
}
