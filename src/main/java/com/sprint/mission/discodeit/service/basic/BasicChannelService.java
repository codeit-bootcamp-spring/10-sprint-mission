package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

@Service
@AllArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    @Override
    public ChannelResponseDto createPrivateChannel(ChannelRequestDto channelCreateDto) { //type Private 일때

        //User 정보를 받아서 User별 ReadStatus 정보를 생성
        User user = userRepository.findById(channelCreateDto.userDto().userId()).orElseThrow(() -> new NoSuchElementException("유저가 없음"));
        Channel channel = channelRepository.save(new Channel(PRIVATE, null,null));
        ReadStatus readStatus = new ReadStatus(user.getId(),channel.getId());
        readStatusRepository.save(readStatus);

        List<UUID> users = new ArrayList<>(List.of(user.getId()));

        return new ChannelResponseDto(
                null,
                null,
                null,
                users
        );

    }
    @Override
    public ChannelResponseDto createPublicChannel(ChannelRequestDto channelCreateDto) { //type PUBLIC 일때

        User user = userRepository.findById(channelCreateDto.userDto().userId()).orElseThrow(() -> new NoSuchElementException("유저가 없음"));
        Channel channel = channelRepository.save(new Channel(PUBLIC, channelCreateDto.name(), channelCreateDto.discription()));
        ReadStatus readStatus = new ReadStatus(user.getId(),channel.getId());
        readStatusRepository.save(readStatus);

        List<UUID> users = new ArrayList<>(List.of(user.getId()));
        return new ChannelResponseDto(
                channel.getName(),
                channel.getDescription(),
                null,
                users

        );

    }

    @Override
    public ChannelResponseDto find(UUID channelId) {

        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException("채널이 없습니다."));

        //해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        Instant lastMessageAt = messageRepository.findLstMessageTimeByChannelId(channelId);

        //PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.(ReadStatus이용)
        List<UUID> userIds = null;
        if(channel.getType() == PRIVATE) {
            userIds = readStatusRepository.findUserIdsByChannelId(channelId);

        }

        return new ChannelResponseDto(
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                userIds
        );
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        //가장 최근 메시지의 시간정보 포함
        //PRIVATE일 경우 참여한 User의 id 정보 포함

        //특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가 메소드명 findAllByUserId
        //PRIVATE 채널은 조회한 User가 참여한 채널만 조회
        List<UUID> privateChannelIds = readStatusRepository.findChannelIdsByUserId(userId);
        List<Channel> privateChannels = channelRepository.findByIds(privateChannelIds);

        //PUBLIC 채널 목록은 전체조회
        List<Channel> publicChannels = channelRepository.findByChannelType(PUBLIC);


        List<Channel> channels = new ArrayList<>();
        channels.addAll(privateChannels);
        channels.addAll(publicChannels);

        // 4. 각 채널을 DTO로 변환
        return channels.stream()
                .map(channel -> {
                    // 가장 최근 메시지의 시간정보 포함
                    Instant lastMessageAt = messageRepository.findLstMessageTimeByChannelId(channel.getId());

                    // PRIVATE일 경우 참여한 User의 id 정보 포함
                    List<UUID> userIds = null;
                    if (channel.getType() == PRIVATE) {
                        userIds = readStatusRepository.findUserIdsByChannelId(channel.getId());
                    }

                    return new ChannelResponseDto(
                            channel.getName(),
                            channel.getDescription(),
                            lastMessageAt,
                            userIds
                    );
                })
                .toList();

    }

    @Override
    public Channel update(UUID channelId,ChannelRequestDto channelUpdateDto) {//DTO를 활용하여 파라미터를 그룹화합니다. 수정대상 객체의 ID 파라미터,수정할 값 파라미터

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        //PRIVATE 채널은 수정할 수 없습니다.
        if(channel.getType() == PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정이 불가능합니다.");
        }

        channel.update(channelUpdateDto.name(), channelUpdateDto.discription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        //관련된 도메인도 같이 삭제(Message,ReadStatus)
        readStatusRepository.deleteByChannelId(channelId);
        messageRepository.deleteAllMessagesByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }
}
