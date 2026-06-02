package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.FieldNotValidException;
import com.sprint.mission.discodeit.exception.RequestNullException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
        if (req == null) {
            throw new RequestNullException();
        }

        // ReadStatus 생성 메서드 시작 TRACE 로그
        log.trace("ReadStatus 생성 메서드 시작: channelId={}, userId={}"
            , req.channelId(), req.userId()
        );

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new ChannelNotFoundException(req.channelId()));
        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> new UserNotFoundException(req.userId()));

        log.trace("ReadStatus 생성 및 영속화 시도:  channelId={}, userId={}"
            , req.channelId(), req.userId());

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus); // ReadStatus 영속화
        ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);

        // Read Status 영속화 성공 INFO 로그
        log.info("[ReadStatus] ReadStatus 생성 및 영속화 성공: readStatusId={}"
            , readStatus.getId());

        return readStatusDto;

    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID rsId) {
        if (rsId == null) {
            throw new FieldNotValidException("rsId");
        }

        log.trace("ReadStatus 조회 메서드 시작: id={}", rsId);

        ReadStatus readStatus =
            readStatusRepository.findById(rsId)
                .orElseThrow(
                    () -> new ReadStatusNotFoundException(rsId));

        log.info("ReadStatus 조회 성공: channelName={}, userName={}",
            readStatus.getChannel().getName(), readStatus.getUser().getUsername());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new FieldNotValidException("userId");
        }

        log.trace("사용자 ID를 통해 해당 사용자의 ReadStatus들을 조회하는 메서드 시작: userId={}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return readStatusRepository
            .findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto).toList();

    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequestDTO req) {
        if (req == null) {
            throw new RequestNullException();
        }
        if (id == null) {
            throw new FieldNotValidException("id");
        }

        // ReadStatus 업데이트 메서드 시작 TRACE 로그
        log.trace("[ReadStatus] ReadStatus 업데이트 메서드 시작: id={}, newLastReadAt={}", id,
            req.newLastReadAt());

        ReadStatus readStatus = getReadstatus(id);

        readStatus.update(req.newLastReadAt(), req.newNotificationEnabled());

        ReadStatus saved = readStatusRepository.save(readStatus);

        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            saved.getLastReadAt(),
            saved.isNotificationEnabled());

    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new FieldNotValidException("id");
        }

        // ReadStatus 삭제 메서드 시작 TRACE 로그
        log.trace("[ReadStatus] ReadStatus 삭제 메서드 시작: id={}", id);

        ReadStatus readStatus = getReadstatus(id);

        log.debug("삭제하려는 ReadStatus의 정보: id={}, channelId={}, userId={}",
            readStatus.getId(), readStatus.getChannel().getId(), readStatus.getUser().getId());

        readStatusRepository.deleteById(id);
        log.info("[ReadStatus] 삭제 성공: id={}", readStatus.getId());
    }

    public ReadStatus getReadstatus(UUID id) {
        return readStatusRepository.findById(id)
            .orElseThrow(() -> new ReadStatusNotFoundException(id));
    }
}
