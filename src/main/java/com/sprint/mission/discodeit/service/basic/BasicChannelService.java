package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PrivateCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.ChannelSummaryResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

//    @RequiredArgsConstructor로 대체
//    public BasicChannelService(ChannelRepository channelRepository) {
//        this.channelRepository = channelRepository;
//    }

    @Override
    public ChannelSummaryResponseDTO create(PublicCreateRequestDTO publicCreateRequestDTO) {
        // DTO 에서 NotBlank 애너테이션으로 검증
        Channel channel = new Channel(
            ChannelType.PUBLIC,
            publicCreateRequestDTO.channelName(),
            publicCreateRequestDTO.description()
        );
        return toChannelSummaryResponseDTO(channelRepository.save(channel));
    }

    // PrivateCreateRequestDTO에 유저 정보가 포함되어 있어야함
    // 유저별 ReadStatus 정보를 생성
    @Override
    public ChannelSummaryResponseDTO create(PrivateCreateRequestDTO privateCreateRequestDTO) {
        // DTO에서 애너테이션으로 검증
        List<UUID> participantsIds = privateCreateRequestDTO.participantIds();
        // Channel 생성
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        // 유저별 ReadStatus 생성
        participantsIds.forEach(
                userId -> readStatusRepository.save(new ReadStatus(userId, channel.getId(), Instant.now()))
        );
        return toChannelSummaryResponseDTO(channelRepository.save(channel));
    }

//    @Override
//    public Channel find(UUID channelId) {
//        return channelRepository.findById(channelId)
//                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
//    }


    @Override
    public ChannelDetailResponseDTO find(UUID channelId) {
        Channel channel = getChannelByIdOrThrow(channelId);
        return toChannelDetailResponseDTO(channel);
    }

    @Override
    public List<ChannelDetailResponseDTO> findAllByUserId(UUID userId) {
        List<ChannelDetailResponseDTO> channelDetailResponseDTOList = new ArrayList<>();
        // 1. channelRepository.findAll()로 전체 채널 정보를 가져온다
        // 2. ReadStatusRepository.findAllByUserId()를 정의 후 List<ReadStatus>를 가져온다
        // 3. List<ReadStatus>를 .map을 통해 List<UUID(user가 속한 private채널 channelid)>로 변환
        // 4. 전체 채널을 돌다가 PRIVATE인 경우 해당 channel.getId()와 List<UUID>를 contains로 비교
        // 4-1. 같지 않다면 continue
        // 5. toChannelDetailResponseDTO(channel)로 DTO만들고 channelDetailResponseDTOList에 add

        List<Channel> allChannel = channelRepository.findAll();
        List<UUID> userPrivateChannelIds = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();
        for (Channel channel : allChannel) {
            if (channel.getType() == ChannelType.PRIVATE && !userPrivateChannelIds.contains(channel.getId())) {
                continue;
            }
            channelDetailResponseDTOList.add(toChannelDetailResponseDTO(channel));
        }
        return channelDetailResponseDTOList;
    }

    @Override
    public ChannelSummaryResponseDTO update(UUID channelId, ChannelUpdateRequestDTO channelUpdateRequestDTO) {
        Channel channel = getChannelByIdOrThrow(channelId);
        // PRIVATE 채널은 수정 불가능
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정 불가능합니다");
        }
        channel.update(
                channelUpdateRequestDTO.newName(),
                channelUpdateRequestDTO.newDescription()
        );
        return toChannelSummaryResponseDTO(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        // 관련 도메인 삭제(Message, ReadStatus)
        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    // ChannelDetailResponseDTO를 만드는 겹치는 코드를 다로 메소드로
    // find/findAll 반환용 DTO를 만드는 메서드
    private ChannelDetailResponseDTO toChannelDetailResponseDTO(Channel channel) {
        // findAll()로 전체 메시지 데이터를 가져와서 원하는 조건으로 필터링 하는 것 보다
        // 그런 작업은 Repository의 책임으로, 가져온 데이터를 어떻게 할지가 Service의 책임
        Message recentMessage = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElseThrow(() -> new NoSuchElementException(channel.getId()+"에 아직 작성된 메시지가 없습니다"));
        // 해당 채널의 가장 최근 메시지 시간정보
        Instant recentMessageTime = recentMessage.getCreatedAt();

        //PRIVATE 채널인 경우 참여한 User id 정보를 포함해야함
        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType()==ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }
        return new ChannelDetailResponseDTO(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                recentMessageTime
        );
    }

    // create, update 단순 결과 반환용 DTO를 만드는 메서드
    private ChannelSummaryResponseDTO toChannelSummaryResponseDTO(Channel channel) {
        return new ChannelSummaryResponseDTO(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }

    // channelRepository.findById()를 통한 반복되는 Channel 조회/예외처리를 중복제거 하기 위한 메서드
    private Channel getChannelByIdOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("channelId:"+channelId+"를 가진 채널을 찾을 수 없습니다"));
    }
}
