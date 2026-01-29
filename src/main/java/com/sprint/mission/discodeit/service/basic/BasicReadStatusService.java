package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
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
class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository ChannelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        //못 찾으면 예외 발생시킴
        if(!userRepository.existsById(readStatusCreateRequestDto.userId())) throw new AssertionError("User not found");
        if(!ChannelRepository.existsById(readStatusCreateRequestDto.channelId())) throw new AssertionError("Message not found");

        //이미 존재하면 예외
        if(readStatusRepository.findAll().stream()
                .anyMatch(readStatus -> readStatus.getChannelID().equals(readStatusCreateRequestDto.channelId()))){
            throw new AssertionError("ReadStatus already exists");
        }

        //생성 및 저장
        return readStatusRepository.save(new ReadStatus(readStatusCreateRequestDto.userId(),
                readStatusCreateRequestDto.channelId()));
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("ReadStatus not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserID().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus update() {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
