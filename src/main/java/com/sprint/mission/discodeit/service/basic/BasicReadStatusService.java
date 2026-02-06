package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
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
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public void create (ReadStatusCreateRequest request){
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
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 읽음 객체가 존재하지 않습니다"));

        return new ReadStatusResponse(
                readStatus.getChannelId(),
                readStatus.getUserId(),
                readStatus.getLastReadAt()
        );
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
       return    readStatusRepository.findAll()
                .stream()
               .filter(rs -> rs.getUserId().equals(userId))
               .map(rs -> new ReadStatusResponse(
                        rs.getChannelId(),
                        rs.getUserId(),
                        rs.getLastReadAt()
                )).toList();

    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 읽음 객체가 존재하지 않습니다"));

        readStatus.updateLastRead();
        readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
