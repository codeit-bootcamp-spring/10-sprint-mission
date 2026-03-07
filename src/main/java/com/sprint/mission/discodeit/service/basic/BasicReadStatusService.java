package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequestDTO req) {
        Objects.requireNonNull(req.channelId(), "유효하지 않은 id입니다!");
        Objects.requireNonNull(req.userId(), "유효하지 않은 id입니다!");

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다!"));
        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다!"));

        try {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
            return ReadStatusMapper.toDto(readStatus);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("ReadStatus 데이터가 중복됩니다!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID rsId) {
        Objects.requireNonNull(rsId, "유효하지 않은 ReadStatus ID 입니다.");
        ReadStatus readStatus =
            readStatusRepository.findById(rsId)
                .orElseThrow(
                    () -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없음."));

        return ReadStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "유효하지 않은 유저 ID 입니다.");
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("유저가 존재하지 않음");
        }

        return readStatusRepository
            .findAllByUserId(userId)
            .stream()
            .map(ReadStatusMapper::toDto).toList();

    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
        Objects.requireNonNull(id, "유효하지 않은 식별자입니다!");

        ReadStatus readStatus = readStatusRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("존재하지 않는 ReadStatus 입니다!")
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
        Objects.requireNonNull(id, "유효하지 않은 ID입니다.");

        readStatusRepository.deleteById(id);

    }
}
