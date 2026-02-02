package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    // Public 채널 생성
    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        request.validate();
        Validation.noDuplicate(
                channelRepository.findAll(),
                ch -> ch.getChannelName().equals(request.name()),
                "이미 존재하는 채널명입니다: " + request.name()
        );

        Channel channel = new Channel(request.name());
        channelRepository.save(channel);
        return assembleChannelResponse(channel);
    }

    // Priavte 채널 생성 (DM... 1대1)
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        request.validate();
        // 채널에 참여한 유저들 확인
        List<User> participants = request.participantUserIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("참여 유저가 없습니다.")))
                .toList();
        // Private 채널 생성!!
        // name 은 알아서? uuid string으로
        String privateName = "PRIVATE: " + UUID.randomUUID();
        Channel channel = new Channel(privateName);
        //채널에 참가자 등록
        for (User u : participants) {
            channel.addParticipant(u);
        }
        // 채널 저장
        channelRepository.save(channel);
        // 참여 유저별 status 저장
        for (User u : participants) {
            ReadStatus readStatus = new ReadStatus(u.getId(), channel.getId());
            readStatusRepository.save(readStatus);
        }
        return assembleChannelResponse(channel);


    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if(userId == null) throw new IllegalArgumentException("userId는 null 일 수 없습니다.");
        // 유저 존재 확인

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다: " + userId));

        List<Channel> allChannel = channelRepository.findAll();

        //public은 전체
        List<Channel> publics = allChannel.stream()
                .filter(ch-> ch.getChannelType() == ChannelType.PUBLIC)
                .toList();

        //private 는 유저가 참여한 채널만
        List<Channel> privates = allChannel.stream()
                .filter(ch-> ch.getChannelType()==ChannelType.PRIVATE)
                .filter(ch->ch.getParticipants().stream().anyMatch(u->u.getId().equals(userId)))
                .toList();

        List<Channel> channels = new ArrayList<>();
        channels.addAll(publics);
        channels.addAll(privates);
        return channels.stream()
                .map(this::assembleChannelResponse)
                .toList();
    }


    @Override
    public ChannelResponse findChannelById(UUID id) {
        if (id == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다: " + id));

        return assembleChannelResponse(channel);
    }

    @Override
    public ChannelResponse updateChannel(ChannelUpdateRequest request) {
        request.validate();

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("수정할 채널이 없습니다: " + request.channelId()));

        // PRIVATE는 수정 불가
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // PUBLIC 중복 이름 검사(본인 채널명으로 바꾸는 경우는 허용)
        Validation.noDuplicate(
                channelRepository.findAll(),
                ch -> ch.getChannelType() == ChannelType.PUBLIC
                        && ch.getChannelName().equals(request.newName())
                        && !ch.getId().equals(channel.getId()),
                "이미 존재하는 채널명입니다: " + request.newName()
        );
        channel.update(request.newName());
        channelRepository.save(channel);

        Instant lastMessageAt = computeLastMessageAt(channel.getId());
        List<UUID> participantIds = computeParticipantIdsIfPrivate(channel);

        return channelMapper.toResponse(channel, lastMessageAt, participantIds);

    }


    //
    @Override
    public void deleteChannel(UUID channelId) {
        // 없는 ID면 예외
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("삭제할 채널이 없습니다: " + channelId));
        // Participant 전부 퇴장
        List<User> participants = new ArrayList<>(channel.getParticipants());
        for (User u : participants) {
            u.leaveChannel(channel);
            userRepository.save(u);
        }
        channelRepository.save(channel);

        //ReadStatus 삭제
        List<ReadStatus> readStatuses = readStatusRepository.findStatusByChannelId(channelId);
        for(ReadStatus rs : readStatuses){
            readStatusRepository.delete(rs.getId());
        }

        //채널의 메세지들 전부 삭제 -> 하기전에 메세지랑 관련되 있는 유저들도 삭제해?야함
        List<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
        for (Message m : messages) {
            User sender = userRepository.findById(m.getSenderId())
                    .orElseThrow(() -> new NoSuchElementException("보낸 유저가 없습니다: " + m.getSenderId()));

            sender.getMessageIds().remove(m.getId());
            userRepository.save(sender);

            //repo에서 메세지 삭제
            messageRepository.delete(m.getId());

        }
        channelRepository.save(channel);
        channelRepository.delete(channelId);
    }




    @Override
    // ChatCoordinator 기능 구현
    public void joinChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다 : " + userId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다 : " + channelId));

        user.joinChannel(channel); // 유저에도 동기화

        // 저장소 반영
        userRepository.save(user);
        channelRepository.save(channel);

    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다 : " + userId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다 : " + channelId));

        user.leaveChannel(channel);
        userRepository.save(user);
        channelRepository.save(channel);
    }






    private ChannelResponse assembleChannelResponse(Channel channel) {
        Instant lastMessageAt = computeLastMessageAt(channel.getId());
        List<UUID> participantIds = computeParticipantIdsIfPrivate(channel);

        return channelMapper.toResponse(channel, lastMessageAt, participantIds);
    }


    private Instant computeLastMessageAt(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }


    private List<UUID> computeParticipantIdsIfPrivate(Channel channel) {
        if (channel.getChannelType() != ChannelType.PRIVATE) return null;

        return channel.getParticipants().stream()
                .map(User::getId)
                .toList();
    }



}


