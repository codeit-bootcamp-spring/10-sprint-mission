package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelDTO.Response createPrivate(UUID creatorId, ChannelDTO.CreatePrivate createRequest) {
        //채널 생성 후 저장
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        //채널 참여자 ReadStatus 생성
        List<UUID> memberIds = new ArrayList<>(createRequest.userIds());
        //채널 생성자를 참여자 목록에 추가
        memberIds.add(creatorId);
        for (UUID memberId : memberIds) {
            ReadStatus status = new ReadStatus(memberId, channel.getId());
            readStatusRepository.save(status);
        }
        return ChannelDTO.Response.of(channel, memberIds, Instant.now());
    }

    @Override
    public ChannelDTO.Response createPublic(ChannelDTO.CreatePublic createRequest) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                createRequest.name(),
                createRequest.description()
        );
        channelRepository.save(channel);
        return ChannelDTO.Response.of(channel, new ArrayList<>(), null);
    }

    @Override
    public ChannelDTO.Response findById(UUID channelId) {
        //채널Id로 채널 객체 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        //채널Id로 ReadStatus를 조회해서 userId를 찾아옴
        List<UUID> allUserIds = new ArrayList<>();
        for (ReadStatus status : readStatusRepository.findAllByChannelId(channelId)) {
            allUserIds.add(status.getUserId());
        }
        //채널Id로 메시지 찾아서 가장 최근 메시지 찾기
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(BaseEntity::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null); //메시지 없으면 null반환
        return ChannelDTO.Response.of(channel, allUserIds, lastMessageAt);
    }

    @Override
    public List<ChannelDTO.Response> findAllByUserId(UUID userId) {
        //public 채널 리스트
        List<ChannelDTO.Response> publicChannels = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .map(Channel::getId)
                .map(this::findById)
                .toList();
        //private 채널 리스트(내가 참여하고 있어야함)
        List<ChannelDTO.Response> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .map(channelRepository::findById)
                .flatMap(Optional::stream)
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .map(Channel::getId)
                .map(this::findById)
                .toList();
        List<ChannelDTO.Response> allChannels = new ArrayList<>();
        allChannels.addAll(publicChannels);
        allChannels.addAll(privateChannels);
        return allChannels;
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 가입할 수 없습니다.");
        }

        readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .ifPresent(status -> {
                    throw new IllegalStateException("이미 가입 중인 채널입니다.");
                });

        ReadStatus status = new ReadStatus(userId, channelId);
        readStatusRepository.save(status);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        ReadStatus status = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new IllegalStateException("가입하지 않은 채널입니다."));
        readStatusRepository.delete(status);
    }

    @Override
    public ChannelDTO.Response update(ChannelDTO.Update request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        Optional.ofNullable(request.name()).ifPresent(channel::updateChannelName);
        Optional.ofNullable(request.description()).ifPresent(channel::updateDescription);

        channelRepository.save(channel);
        return findById(channel.getId());
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        //채널 삭제 시 채널에 있는 메시지 지우기
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
        //메시지 속 첨부파일 삭제
        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .ifPresent(binaryContentRepository::delete);
            }
            messageRepository.delete(message);
        }

        //채널 삭제 시 ReadStatus 지우기
        readStatusRepository.findAllByChannelId(channelId)
                        .forEach(readStatusRepository::delete);

        channelRepository.delete(channel);
    }
}
