package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto.Response create(ReadStatusDto.Create request) {

        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다."));

        readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("이미 수신 정보가 존재합니다.");
                });

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return ReadStatusDto.Response.of(readStatus);
    }

    @Override
    public ReadStatusDto.Response findById(UUID statusId) {
        ReadStatus status = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("수신 정보가 존재하지 않습니다."));
        return ReadStatusDto.Response.of(status);
    }

    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto.Response::of)
                .toList();
    }

    @Override
    public ReadStatusDto.Response update(ReadStatusDto.Update request) {
        ReadStatus status = readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .orElseThrow(() -> new NoSuchElementException("수신 정보가 존재하지 않습니다."));
        status.updateLastReadAt();
        readStatusRepository.save(status);
        return ReadStatusDto.Response.of(status);
    }

    @Override
    public void delete(UUID statusId) {
        ReadStatus status = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("수신 정보가 존재하지 않습니다."));
        readStatusRepository.delete(status);
    }
}
