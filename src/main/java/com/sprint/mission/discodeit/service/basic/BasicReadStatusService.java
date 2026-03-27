package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 생성 요청입니다!");

        // ReadStatus 생성 메서드 시작 TRACE 로그
        log.trace("ReadStatus 생성 메서드 시작: channelId={}, userId={}"
            , req.channelId(), req.userId()
        );

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다!"));
        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다!"));

        log.trace("ReadStatus 생성 및 영속화 시도:  channelId={}, userId={}"
            , req.channelId(), req.userId());

        try {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus); // ReadStatus 영속화
            ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);
            log.info("ReadStatus 생성 및 영속화 성공: userName={}, channelName={}"
                , user.getUsername(), channel.getName());

            return readStatusDto;
        } catch (DataIntegrityViolationException e) {
            log.warn("[ReadStatus] 예외 발생: {}", e.getMessage(), e);
            throw new IllegalStateException("ReadStatus가 DB의 무결성을 해칩니다.", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID rsId) {
        log.trace("ReadStatus 조회 메서드 시작: id={}", rsId);
        Objects.requireNonNull(rsId, "유효하지 않은 ID 입니다!");

        ReadStatus readStatus =
            readStatusRepository.findById(rsId)
                .orElseThrow(
                    () -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다."));

        log.info("ReadStatus 조회 성공: channelName={}, userName={}",
            readStatus.getChannel().getName(), readStatus.getUser().getUsername());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.trace("사용자 ID를 통해 해당 사용자의 ReadStatus들을 조회하는 메서드 시작: userId={}", userId);
        Objects.requireNonNull(userId, "유효하지 않은 사용자 ID입니다.");
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다!");
        }

        return readStatusRepository
            .findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto).toList();

    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다.");

        log.trace("ReadStatus 업데이트 메서드 시작: id={}, newLastReadAt={}", id, req.newLastReadAt());

        ReadStatus readStatus = readStatusRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다.")
        );

        readStatus.update();
        ReadStatus saved = readStatusRepository.save(readStatus);

        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            saved.getLastReadAt());

    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.trace("ReadStatus 삭제 메서드 시작: id={}", id);
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");

        ReadStatus readStatus = readStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다!"));

        log.debug("삭제하려는 ReadStatus의 정보: id={}, channelId={}, userId={}",
            readStatus.getId(), readStatus.getChannel().getId(), readStatus.getUser().getId());

        readStatusRepository.deleteById(id);
        log.info("[ReadStatus] 삭제 성공: id={}", readStatus.getId());

    }
}
