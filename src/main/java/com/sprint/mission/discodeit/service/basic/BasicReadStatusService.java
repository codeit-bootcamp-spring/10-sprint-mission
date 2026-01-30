package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDTO.Response create(ReadStatusDTO.Create request) {

        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("이미 ReadStatus가 존재합니다.");
                });

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return ReadStatusDTO.Response.of(readStatus);
    }

    @Override
    public ReadStatusDTO.Response findById(UUID statusId) {
        ReadStatus status = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ReadStatus입니다."));
        return ReadStatusDTO.Response.of(status);
    }

    @Override
    public List<ReadStatusDTO.Response> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDTO.Response::of)
                .toList();
    }

    @Override
    public ReadStatusDTO.Response update(ReadStatusDTO.Update request) {
        ReadStatus status = readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ReadStatus입니다."));
        status.updateLastReadAt();
        readStatusRepository.save(status);
        return ReadStatusDTO.Response.of(status);
    }

    @Override
    public void delete(UUID statusId) {
        ReadStatus status = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ReadStatus입니다."));
        readStatusRepository.delete(status);
    }
}
