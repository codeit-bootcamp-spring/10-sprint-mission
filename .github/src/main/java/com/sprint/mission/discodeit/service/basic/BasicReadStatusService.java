package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto.Response create(ReadStatusDto.CreateRequest request) {
        // 관련된 채널이나 유저가 존재하지 않을 때 예외 발생
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다."));

        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        // 같은 채널과 유저와 관련된 객체가 이미 존재하면 예외 발생
        if(readStatusRepository.existsByChannelIdAndUserId(request.channelId(), request.userId())) {
            throw new IllegalStateException("이미 채널과 유저의 상태가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.channelId(), request.userId());
        return convertToResponse(readStatusRepository.save(readStatus));

    }

    @Override
    public ReadStatusDto.Response find(UUID id) {
        return readStatusRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 상태가 존재하지 않습니다."));
    }

    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ReadStatusDto.Response update(ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 상태가 존재하지 않습니다."));

        Optional.ofNullable(request.lastReadMessageId())
                .ifPresent(readStatus::updateLastReadMessage);

        return convertToResponse(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID id) {
        if(!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("존재하지 않는 Id 입니다.");
        }
        readStatusRepository.deleteById(id);

    }
    private ReadStatusDto.Response convertToResponse(ReadStatus readStatus) {
        return new ReadStatusDto.Response(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadMessageId(),
                readStatus.getUpdatedAt()
        );
    }

}
