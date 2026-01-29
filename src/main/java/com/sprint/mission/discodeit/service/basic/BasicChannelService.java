package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channeldto.ChannelViewDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateDTO req) {
        if(channelRepository.findAll()
                .stream()
                .anyMatch(c -> req.name().equals(c.getName())))
        {
            throw new IllegalStateException("해당 채널의 이름이 중복됩니다.");
        }

        Objects.requireNonNull(req.channelType(), "유효하지 않은 채널 타입입니다.");
        Objects.requireNonNull(req.name(), "유효하지 않은 채널 이름입니다.");
        Objects.requireNonNull(req.description(), "유효하지 않은 채널 설명입니다.");

        return channelRepository.save(new Channel(req.channelType(), req.name(), req.description()));
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateDTO req) {
        Channel channel = new Channel(ChannelType.PRIVATE,
                null,
                null);

        Channel saved = channelRepository.save(channel);

        req.users()
                .forEach(u -> {
                    userRepository.findById(u.getId()).orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));
                    ReadStatus readStatus = new ReadStatus(u.getId(),
                            saved.getId()
                    );
                    readStatusRepository.save(readStatus);
                });

        return saved;
    }


    // 채널 ID를 매개변수로 받고, ChannelViewDTO를 만들어서 정보를 공개하는 메소드
    // ChannelViewDTO는 가장 최근 메세지의 시간 정보(createdAt, updatedAT)과
    // ChannelType 여부에 따라 User 정보를 담는다.
    @Override
    public ChannelViewDTO find(UUID channelId) {

        // 채널 ID가 존재하는지 검증
        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(() -> new IllegalStateException("해당 채널 ID가 존재하지 않습니다."));

        // 메세지 리포지토리에서 channelID를 통해 메세지 리스트를 뽑고 stream ->
        // max를 사용하여 가장 큰(최근) createdAt을 가진 메시지를 뽑고
        // 메시지가 존재하지 않으면 null 리턴? <- 추후 바꿔야할듯
        Message message = Objects.requireNonNull(messageRepository.findByChannelId(channelId)
                        .stream()
                        .max(Comparator.comparing(Message::getUpdatedAt))
                        .orElse(null));

        // 만약 채널 타입이 PRIVATE이면...
        if(channel.getType() == ChannelType.PRIVATE){
            // ChannelViewDTO 생성 및 반환
            return new ChannelViewDTO(
                    message.getCreatedAt(),
                    message.getUpdatedAt(),
                    // Private이면 유저 ID 정보 리스트를 반환한다.
                    channel.getUserList()
            );
        }

        // 채널 타입이 Public이라면...
        return new ChannelViewDTO(
                message.getCreatedAt(),
                message.getUpdatedAt(),
                Collections.emptyList() // 빈 리스트를 전달한다.
                );
        }

    // 채널 리스트에서 각 채널마다 가장 최근 메세지의 시간 정보를 포함하고,
    // userID 정보를 포함한 ChannelViewDTO의 리스트를 반환하는 메소드

    @Override
    public List<ChannelViewDTO> findAll() {
        List<ChannelViewDTO> list = new ArrayList<>();

            // 1. 채널 리포지토리에서 findAll() 호출 -> 채널 리스트 반환
            // 2. 채널 리스트에서 forEach 호출 -> 각 채널마다 최근 메시지 시간 정보들과 userID 정보를 구조화
            // 3. 구조화한 것 그대로 ChannelViewDTO 생성 및 list에 추가 (private일 경우)
            channelRepository
                    .findAll()
                    .forEach(
                            c -> {
                                Message message = messageRepository.findById(c.getId()).stream().max(Comparator.comparing(Message::getUpdatedAt)).get();

                                // 채널 타입이 Public이면..
                                if(c.getType() == ChannelType.PUBLIC){
                                    list.add(new ChannelViewDTO(
                                        message.getCreatedAt(),
                                        message.getUpdatedAt(),
                                        null)
                                    );
                                }
                                // 채널 타입이 Private이면..
                                else{
                                    list.add(new ChannelViewDTO(
                                            message.getCreatedAt(),
                                            message.getUpdatedAt(),
                                            Collections.emptyList() // userID 정보(리스트) DTO에 삽입
                                    ));
                                }
                            }
                    );

            return list; // DTO 리스트 반환
        }


    @Override
    public Channel join(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        user.joinChannel(channelId);
        userRepository.save(user);

        return channel;
    }

    @Override
    public Channel leave(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        user.leaveChannel(channelId);
        userRepository.save(user);

        return channel;
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        channelRepository.deleteById(channelId);
    }
}
