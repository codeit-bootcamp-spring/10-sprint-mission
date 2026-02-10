package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.message.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.message.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.message.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;


    @Override
    public ReadStatusResponse create (ReadStatusCreateRequest request){
        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Channel channel = channelRepository
                .findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        readStatusRepository.findByUserAndChannelId(request.userId(), request.channelId())
                .ifPresent(readStatus -> {
                    throw new IllegalArgumentException("이미 읽음 객체가 존재합니다.");
                });

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return readStatusMapper.convertToResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 읽음 객체가 존재하지 않습니다"));

        return readStatusMapper.convertToResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
       return    readStatusRepository.findAll()
                .stream()
               .filter(rs -> rs.getUserId().equals(userId))
               .map(rs -> new ReadStatusResponse(
                        rs.getUserId(),
                        rs.getChannelId(),
                        rs.getLastReadAt()
                )).toList();

    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 읽음 객체가 존재하지 않습니다"));

        readStatus.updateLastRead();
        readStatusRepository.save(readStatus);
        return readStatusMapper.convertToResponse(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
