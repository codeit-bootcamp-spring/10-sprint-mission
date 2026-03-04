package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.*;
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
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public ChannelDto.channelResponse createChannel(ChannelDto.channelCreatePrivateRequest channelPrivateReq) {
        // title과 description 불필요에 따른 title 미검증
        List<UUID> participantIds = channelPrivateReq.participantIds();

        // 참여자 목록의 유저가 user DB에 있는지 확인
        participantIds.forEach(userId -> {
            userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        });
        Channel privateChannel = Channel.of(participantIds);

        // ReadStatus 생성
        participantIds.forEach(userId -> {
            readStatusRepository.save(new ReadStatus(userId, privateChannel.getId()));
        });

        channelRepository.save(privateChannel);
        return toResponse(privateChannel);
    }

    @Override
    public ChannelDto.channelResponse createChannel(ChannelDto.channelCreatePublicRequest channelPublicReq) {
        validateDuplicateTitle(channelPublicReq.title());

        Channel publicChannel = Channel.of(channelPublicReq.title(), channelPublicReq.description());
        channelRepository.save(publicChannel);
        return toResponse(publicChannel);
    }

    @Override
    public ChannelDto.channelResponse findChannel(UUID uuid) {
        return channelRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    public ChannelDto.channelResponse findChannelByTitle(String title) {
        return channelRepository.findAll().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .map(this::toResponse)
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    public List<ChannelDto.channelResponse> findAllByUserId(UUID userId) {
        getUserOrThrow(userId);

        return channelRepository.findAll().stream()
                // PUBLIC 채널 전부 + userId가 참여한 PRIVATE 채널
                .filter(c -> Objects.equals(c.getChannelType(), ChannelType.PUBLIC)
                                || c.getParticipants().contains(userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelDto.channelResponse updateChannel(UUID uuid, ChannelDto.channelUpdatePublicRequest channelReq) {
        Channel channel = getChannelOrThrow(uuid);

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new BusinessLogicException(ErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE);
        }

        // title 중복성 검사
        if (channelReq.title() != null && !Objects.equals(channel.getTitle(), channelReq.title()))
            validateDuplicateTitle(channelReq.title());

        Optional.ofNullable(channelReq.title()).ifPresent(channel::updateTitle);
        Optional.ofNullable(channelReq.description()).ifPresent(channel::updateDescription);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        return toResponse(channel);
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Channel channel = getChannelOrThrow(uuid);
        channel.getParticipants()
                .forEach(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
                    user.removeJoinedChannels(channel.getId());
                    user.updateUpdatedAt();
                    userRepository.save(user);
                });

        readStatusRepository.deleteAllByChannelId(channel.getId());

        // 메시지 내부의 첨부파일 삭제
        messageRepository.findAllByChannelId(channel.getId())
            .forEach(m -> m.getAttachmentIds()
                    .forEach(bcId -> {
                        m.removeAttachmentId(bcId);
                        binaryContentRepository.deleteById(bcId);
            }));

        messageRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(uuid);
    }

//    @Override
//    public void joinChannel(UUID channelId, UUID userId) {
//        Channel channel = getChannelOrThrow(channelId);
//        User user = getUserOrThrow(userId);
//
//        if (channel.getParticipants().stream()
//                .anyMatch(u -> Objects.equals(u, userId))) {
//            throw new BusinessLogicException(ErrorCode.USER_ALREADY_IN_CHANNEL);
//        }
//
//        if (user.getJoinedChannels().stream()
//                .anyMatch(u -> Objects.equals(u, channelId))) {
//            throw new BusinessLogicException(ErrorCode.USER_ALREADY_IN_CHANNEL);
//        }
//
//        channel.addParticipant(userId);
//        channel.updateUpdatedAt();
//        channelRepository.save(channel);
//
//        user.addJoinedChannels(channelId);
//        user.updateUpdatedAt();
//        userRepository.save(user);
//
//        readStatusRepository.findAllByUserId(userId).stream()
//                .filter(r -> Objects.equals(r.getChannelId(), channelId))
//                .findFirst()
//                .ifPresent(r -> { throw new BusinessLogicException(ErrorCode.READSTATUS_ALREADY_EXISTS); });
//        ReadStatus readStatus = new ReadStatus(userId, channelId);
//        readStatusRepository.save(readStatus);
//    }
//
//    @Override
//    public void leaveChannel(UUID channelId, UUID userId) {
//        Channel channel = getChannelOrThrow(channelId);
//        User user = getUserOrThrow(userId);
//
//        if (channel.getParticipants().stream()
//                .noneMatch(u -> Objects.equals(u, userId))) {
//            throw new BusinessLogicException(ErrorCode.USER_NOT_IN_CHANNEL);
//        }
//
//        if (user.getJoinedChannels().stream()
//                .noneMatch(u -> Objects.equals(u, channelId))) {
//            throw new BusinessLogicException(ErrorCode.USER_NOT_IN_CHANNEL);
//        }
//
//        channel.removeParticipant(userId);
//        channel.updateUpdatedAt();
//        channelRepository.save(channel);
//
//        user.removeJoinedChannels(channelId);
//        user.updateUpdatedAt();
//        userRepository.save(user);
//
//        ReadStatus readStatus = readStatusRepository.findAllByUserId(userId).stream()
//                .filter(r -> Objects.equals(r.getChannelId(), channelId))
//                .findFirst()
//                .orElseThrow(() -> new BusinessLogicException(ErrorCode.READSTATUS_NOT_FOUND));
//        readStatusRepository.deleteById(readStatus.getId());
//    }

    private void validateDuplicateTitle(String title) {
        channelRepository.findAll().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .findFirst()
                .ifPresent(u -> { throw new BusinessLogicException(ErrorCode.DUPLICATE_TITLE); });
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    private ChannelDto.channelResponse toResponse(Channel channel) {
        Instant lastMessageAt  = messageRepository.findAllByChannelId(channel.getId()).stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt).reversed())
                .limit(1)
                .findFirst()
                .map(BaseEntity::getCreatedAt)
                .orElse(null);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            participantIds = channel.getParticipants().stream().toList();
        }

        return new ChannelDto.channelResponse(channel.getId(), channel.getCreatedAt(), channel.getUpdatedAt(),
                channel.getChannelType(), channel.getTitle(), channel.getDescription(),
                participantIds, lastMessageAt);
    }
}
