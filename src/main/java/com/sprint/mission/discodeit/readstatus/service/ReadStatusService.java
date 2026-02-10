package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.channel.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusUpdateInfo;
import com.sprint.mission.discodeit.channel.Channel;
import com.sprint.mission.discodeit.readstatus.ReadStatus;
import com.sprint.mission.discodeit.readstatus.exception.ReadStatusDuplicationException;
import com.sprint.mission.discodeit.readstatus.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.user.User;
import com.sprint.mission.discodeit.readstatus.ReadStatusMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusInfo createReadStatus(ReadStatusCreateInfo statusInfo) {
        User user = userRepository.findById(statusInfo.userId())
                .orElseThrow(UserNotFoundException::new);
        Channel channel = channelRepository.findById(statusInfo.channelId())
                .orElseThrow(ChannelNotFoundException::new);

        if(readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                .isPresent()) throw new ReadStatusDuplicationException();

        ReadStatus readStatus = new ReadStatus(channel.getId(), user.getId());
        readStatusRepository.save(readStatus);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public ReadStatusInfo find(UUID statusId) {
        ReadStatus readStatus = readStatusRepository.findById(statusId)
                .orElseThrow(ReadStatusNotFoundException::new);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public List<ReadStatusInfo> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatusMapper::toReadStatusInfo)
                .toList();
    }

    public ReadStatusInfo updateReadStatus(UUID statusId) {
        ReadStatus readStatus = readStatusRepository.findById(statusId)
                .orElseThrow(ReadStatusNotFoundException::new);
        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public ReadStatusInfo updateReadStatus(ReadStatusUpdateInfo updateInfo) {
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(updateInfo.userId(), updateInfo.channelId())
                .orElseThrow();
        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);
        return ReadStatusMapper.toReadStatusInfo(readStatus);
    }

    public void deleteReadStatus(UUID statusId) {
        readStatusRepository.deleteById(statusId);
    }
}
