package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.IsMessageReadResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.mapper.readstatus.ReadStatusResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    //
    private final ReadStatusResponseMapper readStatusResponseMapper;
    private final ChannelResponseMapper channelResponseMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        //못 찾으면 예외 발생시킴
        if(!userRepository.existsById(readStatusCreateRequestDto.userId())) throw new AssertionError("User not found");
        if(!channelRepository.existsById(readStatusCreateRequestDto.channelId())) throw new AssertionError("Channel not found");

        //이미 존재하면 예외
        if(readStatusRepository.findAll().stream()
                .anyMatch(readStatus -> readStatus.getChannelID().equals(readStatusCreateRequestDto.channelId()) &&
                        readStatus.getUserID().equals(readStatusCreateRequestDto.userId()) ))
        {
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

    public IsMessageReadResponseDto findByUserIdAndMessageId(UUID userId, UUID messageId){

        Message message = messageRepository.findById(messageId)
                .orElseThrow();

        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, message.getChannelId())
                .orElseThrow(() -> new AssertionError("ReadStatus not found"));

        boolean isRead = message.getCreatedAt().isBefore(readStatus.getLastUserReadTimeInChannel());

        return new IsMessageReadResponseDto(isRead, userId, messageId);
    }

    @Override
    public List<IsMessageReadResponseDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) throw new AssertionError("User not found");

        List<ReadStatus> statusList = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserID().equals(userId))
                .toList();

        Map<UUID, ReadStatus> readStatusByChannelId = statusList.stream()
                .collect(Collectors.toMap(
                        ReadStatus::getChannelID,
                        Function.identity(),
                        (a, b) -> a
                ));

        return messageRepository.findAll().stream()
                .filter(message -> readStatusByChannelId.containsKey(message.getChannelId()))
                .map(message -> {
                    ReadStatus rs = readStatusByChannelId.get(message.getChannelId());
                    boolean isRead = message.getCreatedAt().isBefore(rs.getLastUserReadTimeInChannel());
                    return new IsMessageReadResponseDto(isRead, userId, message.getId());
                })
                .toList();
    }


    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus targetReadStatus = readStatusRepository.findByUserIdAndChannelId(
                readStatusUpdateRequestDto.userId(), readStatusUpdateRequestDto.channelId())
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
