package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusResponseDto create(ReadStatusRequestDto readStatusCreateDto) {

        //관련된 채널이나 User가 존재하지 않는다면 예외를 발생 시킵니다.
        if(userRepository.existsById(readStatusCreateDto.userId()) || channelRepository.existsById(readStatusCreateDto.channelId())) {
            throw new IllegalArgumentException("해당 유저나 채널이 없습니다.");
        }
        if(readStatusRepository.existsById(readStatusCreateDto.userId(),readStatusCreateDto.channelId())) {
            throw new IllegalArgumentException("이미 ReadStatus가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                readStatusCreateDto.userId(),
                readStatusCreateDto.channelId()
        );

        readStatusRepository.save(readStatus);

        return  new ReadStatusResponseDto(
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId()
        );
    }

    public ReadStatusResponseDto find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);

        return new ReadStatusResponseDto(
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId()
        );
    }



}
