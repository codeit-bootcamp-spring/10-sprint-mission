package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelFindResponse;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.channel.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    // 필드
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository ReadStatusRepository;

    private final ChannelMapper channelMapper;

    // public Channel 생성
    @Override
    public ChannelResponse createPublic(ChannelCreateRequestPublic request) {
        // 같은 이름 존재 check
        channelRepository.findAll().stream()
                .filter(ch -> ch.getName().equals(request.name()))
                .findFirst()
                .ifPresent(ch -> {
                    throw new IllegalArgumentException("Already Present name");
                });

        Channel channel = channelMapper.toEntity(request);
        Channel savedChannel = channelRepository.save(channel);
        // [저장]
        return channelMapper.toResponse(savedChannel);
    }

    // private Channel 생성 : 이름, description 생략 채널 참여 유저 정보 생성 + 유저 별 readStatus 정보
    @Override
    public ChannelResponse createPrivate(ChannelCreateRequestPrivate request) {
        // channel 생성
        Channel channel = channelMapper.toEntity(request);

        // private channel의 userList
        List<User> users = new ArrayList<>(request.users());

        // ReadStatus 생성 -> 저장
        for(User user : users) {
            ReadStatus status = new ReadStatus(channel.getId(), user.getId());
            ReadStatusRepository.save(status);
        }

        Channel savedChannel = channelRepository.save(channel);

        return channelMapper.toResponse(savedChannel);
    }

    @Override
    public ChannelFindResponse find(UUID id) {
        // channel 조회
        Channel channel = channelRepository.find(id);

        // 최근 메시지의 시간
        Instant lastCreatedAt = channel.getMessageList().stream()
                                .map(Base::getCreatedAt)
                                .max(Instant::compareTo)
                                .orElse(null);

        if (lastCreatedAt == null) {
            throw new IllegalStateException("lastCreatedAt is null");
        }

        List<UUID> userIDs = null;
        // private일 경우
        if (channel.getDescriptions().equals("Private")){
            userIDs = channel.getMembersList().stream()
                    .map(User::getId)
                    .toList();
        }

        return new ChannelFindResponse(
                channel.getId(), channel.getName(), channel.getDescriptions(), lastCreatedAt, userIDs
        );
    }

    @Override
    public List<ChannelFindResponse> findAllByUserID(UUID userID) {
        // ChannelRepo channel 전체 조회
        List<Channel> channels = new ArrayList<>(channelRepository.findAll());
        // Channel전체 정보 담을 List 선언
        List<ChannelFindResponse> channelResponses = new ArrayList<>();

        // Channel type에 따라 channelReponses에 저장
        for (Channel channel : channels) {
            boolean isPublic = channel.getDescriptions().equals("Public");
            boolean isPrivate = channel.getDescriptions().equals("Private");
            boolean isMember = channel.getMembersList().stream().anyMatch(user -> user.getId().equals(userID));
            if (isPublic || (isPrivate && isMember)) {
                // channel의 가장 최근 시간
                Instant lastCreatedAt = channel.getMessageList().stream()
                        .map(Base::getCreatedAt)
                        .max(Instant::compareTo)
                        .orElse(null);

                // public일 경우 null
                List<UUID> userIDs = null;
                // private일 경우 userIDs List 생성
                if (channel.getDescriptions().equals("Private")) {
                    userIDs = channel.getMembersList().stream()
                            .map(User::getId)
                            .toList();
                }

                channelResponses.add(
                        new ChannelFindResponse(
                                channel.getId(),
                                channel.getName(),
                                channel.getDescriptions(),
                                lastCreatedAt,
                                userIDs
                        )
                );
            }
        }

        return channelResponses;
    }

    @Override
    public ChannelResponse updateName(ChannelUpdateRequest request) {
        // [저장] , 조회
        Channel channel = channelRepository.find(request.channelID());

        // 비즈니스
        channel.updateName(request.name());
        Channel savedChannel = channelRepository.save(channel);

        Set<UUID> userIds = new HashSet<>();
        // user에서도 변경된 이름으로 save되어야 함.
        for (User user : channel.getMembersList()) {
            for (Channel c : user.getChannelsList()) {
                if (c.getId().equals(request.channelID())) {
                    c.updateName(request.name());
                    userIds.add(user.getId());
                    break;
                }
            }
        }
        // 변경사항 저장 -> find에서 load하고
        for (UUID userId : userIds) {
            userRepository.save(userRepository.find(userId).get());
        }

        Set<UUID> messageIds = new HashSet<>();


        for (Message message : channel.getMessageList()) {
            message.getChannel().updateName(request.name());
            messageIds.add(message.getId());
        }

        // 변경사항 저장
        for (UUID messageId : messageIds) {
            messageRepository.save(messageRepository.find(messageId).get());
        }

        return channelMapper.toResponse(savedChannel);
    }

    @Override
    public void deleteChannel(UUID channelID) {
        // [저장]
        Channel channel = channelRepository.find(channelID);

        // [비즈니스]
        List<User> members = new ArrayList<>(channel.getMembersList());

        // [비즈니스], 여기 삭제가 잘 되려나 ??
        members.forEach(user -> user.leaveChannel(channel));

        ReadStatusRepository.deleteByChannelID(channelID);

        // [비즈니스]
        List<Message> messageList = new ArrayList<>(channel.getMessageList());
        messageList.forEach(message -> messageRepository.deleteMessage(message.getId()));

        // [저장]
        channelRepository.deleteChannel(channel.getId());
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {

        // [저장], 조회
        Channel channel = channelRepository.find(channelID);
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        if (channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is already in this channel." + channelID);
        }

        // [비즈니스]
        channel.addMember(user);

        // [비즈니스]
        user.joinChannel(channel);

        // [저장], 변경사항 저장
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        Channel channel = channelRepository.find(channelID);
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // 객체 contains(user) 금지 -> id 기준으로 멤버십 확인
        boolean isMember = channel.getMembersList().stream()
                .anyMatch(u -> u.getId().equals(userID));

        if (!isMember) {
            throw new IllegalArgumentException("User is not in this channel." + channelID);
        }

        // 객체 remove(user) 금지 -> id 기준 제거
        channel.getMembersList().removeIf(u -> u.getId().equals(userID));

        // 객체 remove(channel) 금지 -> id 기준 제거
        user.getChannelsList().removeIf(c -> c.getId().equals(channelID));

        // 기존 삭제의 원인 (채널/메시지/유저 연쇄 삭제 원인) -> User가 해당 Channel에 보낸 메시지도 삭제되어야 하지 않나?
        // List<Message> messageList = new ArrayList<>(user.getMessageList());
        // messageList.stream()
        //        .filter(msg -> msg.getChannel().equals(channel))
        //        .forEach(msg -> messageService.deleteMessage(msg.getId()));

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public List<String> findMembers(UUID channelID) {
        Channel channel = channelRepository.find(channelID);
        // userService의 find로 user 객체 최신화 필요
        return channel.getMembersList().stream()
                .map(user -> userRepository.find(user.getId())
                        .orElseThrow(()-> new IllegalArgumentException("User not found: "+ user.getId())))
                .map(User::getName)
                .collect(toList());
    }
}
