package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.readstatus.ReadStatusResponseMapper;
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
    //
    private final ReadStatusResponseMapper readStatusResponseMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        //못 찾으면 예외 발생시킴
        if(!userRepository.existsById(readStatusCreateRequestDto.userId())) throw new AssertionError("User not found");
        if(!ChannelRepository.existsById(readStatusCreateRequestDto.channelId())) throw new AssertionError("Message not found");

        //이미 존재하면 예외
        if(readStatusRepository.findAll().stream()
                .anyMatch(readStatus -> readStatus.getChannelID().equals(readStatusCreateRequestDto.channelId()))){
            throw new AssertionError("ReadStatus already exists");
        }

        //생성 및 저장
        ReadStatus readStatus = readStatusRepository.save(new ReadStatus(readStatusCreateRequestDto.userId(),
                readStatusCreateRequestDto.channelId()));

        return readStatusResponseMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("ReadStatus not found"));
        return readStatusResponseMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatusResponseDto> readStatusList = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserID().equals(userId))
                .map(readStatusResponseMapper::toDto)
                .toList();

        return readStatusList;

    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus targetReadStatus = readStatusRepository.findById(readStatusUpdateRequestDto.readStatusId())
                        .orElseThrow(()-> new AssertionError("ReadStatus not found"));

        targetReadStatus.setLastUserReadTimeInChannel(readStatusUpdateRequestDto.lastRead());

        readStatusRepository.save(targetReadStatus);

        return readStatusResponseMapper.toDto(targetReadStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
