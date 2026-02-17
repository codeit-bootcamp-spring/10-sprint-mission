package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto.response createReadStatus(ReadStatusDto.createRequest createReq) {
        User user = userRepository.findById(createReq.userId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        Channel channel = channelRepository.findById(createReq.channelId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));

        readStatusRepository.findAllByUserId(user.getId()).stream()
                .filter(r -> Objects.equals(r.getChannelId(), channel.getId()))
                .findFirst()
                .ifPresent(r -> { throw new IllegalStateException("이미 존재하는 readStatus입니다"); });

        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);
        return toResponse(readStatus);
    }

    @Override
    public ReadStatusDto.response findById(UUID uuid) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 readStatus입니다"));

        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusDto.response> findAllByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReadStatusDto.response updateReadStatus(UUID uuid, ReadStatusDto.updateRequest updateReq) {
        ReadStatus readStatus = readStatusRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 readStatus입니다"));

        readStatus.updateLastReadAt(updateReq.lastReadAt());
        readStatus.updateUpdatedAt();
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public void deleteReadStatusById(UUID uuid) {
        readStatusRepository.deleteById(uuid);
    }

    private ReadStatusDto.response toResponse(ReadStatus readStatus) {
        return new ReadStatusDto.response(readStatus.getId(), readStatus.getCreatedAt(), readStatus.getUpdatedAt(),
                readStatus.getUserId(), readStatus.getChannelId(), readStatus.getLastReadAt());
    }
}

