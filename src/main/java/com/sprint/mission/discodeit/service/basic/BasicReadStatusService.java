package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusCreateInput;
import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusUpdateInput;
import com.sprint.mission.discodeit.dto.readstatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public ReadStatus createReadStatus(ReadStatusCreateInput input) {
        // user 객체 존재 확인
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        // channel 객체 존재 확인
        Channel channel = channelRepository.findById(input.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        if (readStatusRepository.existReadStatus(input.userId(), input.channelId())) {
            throw new IllegalStateException("이미 존재하는 ReadStatus가 있습니다.");
        }

        boolean isNotJoin = channel.getChannelMembersList().stream()
                .noneMatch(member -> member.getId().equals(input.userId()));
        if (isNotJoin) {
            throw new IllegalStateException("채널에 참여하지 않은 유저입니다.");
        }

        ReadStatus readStatus = new ReadStatus(input.userId(), input.channelId());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        return validateAndGetReadStatusByReadStatusId(readStatusId);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        // user ID null & user 객체 존재 확인
        validateUserByUserId(userId);
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        List<ReadStatusResponse> readStatusInfos = new ArrayList<>();
        for (ReadStatus readStatus : readStatuses) {
            readStatusInfos.add(new ReadStatusResponse(
                    readStatus.getId(), readStatus.getUserId(),
                    readStatus.getChannelId(), readStatus.getCreatedAt(),
                    readStatus.getUpdatedAt(), readStatus.getLastReadTime()
            ));
        }

        return readStatusInfos;
    }

    @Override
    public ReadStatus updateReadStatus(ReadStatusUpdateInput input) {
        ReadStatus readStatus = readStatusRepository.findById(input.readStatusId())
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus가 없습니다."));

        readStatus.updateLastReadTime(input.lastReadTime());
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
