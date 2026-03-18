package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper mapper;

    @Override
    public ReadStatusDto createReadStatus(ReadStatusDto.ReadStatusCreateRequest createReq) {
        User user = userRepository.findById(createReq.userId())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        Channel channel = channelRepository.findById(createReq.channelId())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));

        readStatusRepository.findAllByUserId(user.getId()).stream()
                .filter(r -> Objects.equals(r.getChannel().getId(), channel.getId()))
                .findFirst()
                .ifPresent(r -> { throw new BusinessLogicException(ErrorCode.READSTATUS_ALREADY_EXISTS); });

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus);
        return toResponse(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto findById(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.READSTATUS_NOT_FOUND));

        return toResponse(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReadStatusDto updateReadStatus(UUID uuid, ReadStatusDto.ReadStatusUpdateRequest updateReq) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.READSTATUS_NOT_FOUND));

        readStatus.updateLastReadAt(updateReq.newLastReadAt());
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public void deleteReadStatusById(UUID uuid) {
        readStatusRepository.deleteById(uuid);
    }

    private ReadStatusDto toResponse(ReadStatus readStatus) {
        return mapper.toDto(readStatus);
    }
}

