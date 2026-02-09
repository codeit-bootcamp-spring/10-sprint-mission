package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusUpdateInfo;
import com.sprint.mission.discodeit.channel.Channel;
import com.sprint.mission.discodeit.readstatus.ReadStatus;
import com.sprint.mission.discodeit.user.User;
import com.sprint.mission.discodeit.readstatus.ReadStatusMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusInfo createReadStatus(ReadStatusCreateInfo statusInfo) {
        User user = userRepository.findById(statusInfo.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
        Channel channel = channelRepository.findById(statusInfo.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재지 않습니다."));

        if(readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                .isPresent()) throw new IllegalStateException("읽기 상태가 이미 존재합니다.");

        ReadStatus readStatus = new ReadStatus(channel.getId(), user.getId());
        readStatusRepository.save(readStatus);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public ReadStatusInfo find(UUID statusId) {
        ReadStatus readStatus = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 읽기 상태가 존재하지 않습니다."));
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public List<ReadStatusInfo> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatusMapper::toReadStatusInfo)
                .toList();
    }

    public ReadStatusInfo updateReadStatus(ReadStatusUpdateInfo updateInfo) {
        ReadStatus readStatus = readStatusRepository.findById(updateInfo.statusId())
                .orElseThrow(() -> new NoSuchElementException("해당 읽기 상태가 존재하지 않습니다."));
        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public void deleteReadStatus(UUID statusId) {
        readStatusRepository.deleteById(statusId);
    }
}
