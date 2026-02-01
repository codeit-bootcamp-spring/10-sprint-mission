package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
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
    public ReadStatus createReadStatus(ReadStatusCreateRequest request) {
        // user ID null & user 객체 존재 확인
        validateUserByUserId(request.userId());
        // Channel ID null & channel 객체 존재 확인
        validateChannelByChannelId(request.channelId());

        if (readStatusRepository.existReadStatus(request.userId(), request.channelId())) {
            throw new IllegalStateException("이미 존재하는 ReadStatus가 있습니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        return validateAndGetReadStatusByReadStatusId(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        // user ID null & user 객체 존재 확인
        validateUserByUserId(userId);

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus updateReadStatus(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = validateAndGetReadStatusByReadStatusId(request.readStatusId());

        readStatus.updateLastReadTime(request.lastReadTime());
        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        validateReadStatusByReadStatusId(readStatusId);
        readStatusRepository.delete(readStatusId);
    }

    //// validation
    // user ID null & user 객체 존재 확인
    public void validateUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // Channel ID null & channel 객체 존재 확인
    public void validateChannelByChannelId(UUID channelId) {
        ValidationMethods.validateId(channelId);
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    public ReadStatus validateAndGetReadStatusByReadStatusId(UUID readStatusId) {
        ValidationMethods.validateId(readStatusId);
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus가 없습니다."));
    }
    public void validateReadStatusByReadStatusId(UUID readStatusId) {
        ValidationMethods.validateId(readStatusId);
        readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus가 없습니다."));
    }
}
