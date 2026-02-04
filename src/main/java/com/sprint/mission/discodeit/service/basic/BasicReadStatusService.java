package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final EntityFinder entityFinder;

    public ReadStatusDto.ReadStatusResponse create(UUID userId, UUID channelId){
        // 입력값 유효성 검증
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");
        // 생성하려는 ReadStatus 가 이미 존재할 때
        if(readStatusRepository.findAll().stream().anyMatch(readStatus ->
                readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)))
            throw new IllegalArgumentException("해당 상태 객체가 이미 존재합니다.");
        // 찾는 유저와 채널이 없을 때
        User user = entityFinder.getUser(userId);
        Channel channel = entityFinder.getChannel(channelId);
        // 새 객체 생성
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        readStatusRepository.save(readStatus);

        return ReadStatusDto.ReadStatusResponse.from(readStatus);
    }

    public ReadStatusDto.ReadStatusResponse findById(UUID readStatusId){
        Objects.requireNonNull(readStatusId, "읽음상태 객체 Id가 유효하지 않습니다.");
        ReadStatus readStatus = entityFinder.getReadStatus(readStatusId);
        return ReadStatusDto.ReadStatusResponse.from(readStatus);
    }

    public List<ReadStatusDto.ReadStatusResponse> findAllByUserId(UUID userId){
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto.ReadStatusResponse::from).toList();
    }

    public ReadStatusDto.ReadStatusResponse update(UUID readStatusId, Instant lastReadTime){
        ReadStatus readStatus = entityFinder.getReadStatus(readStatusId);
        Optional.ofNullable(lastReadTime).ifPresent(readStatus::updateLastReadTime);
        readStatusRepository.save(readStatus);
        return ReadStatusDto.ReadStatusResponse.from(readStatus);
    }

    public void delete(UUID readStatusId){
        entityFinder.getReadStatus(readStatusId);
        readStatusRepository.delete(readStatusId);
    }

}

