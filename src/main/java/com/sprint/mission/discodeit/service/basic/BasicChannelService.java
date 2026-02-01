package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        // `*Controller` 만들면 삭제
        User owner = validateAndGetUserByUserId(request.ownerId());
//        ValidationMethods.validateNullBlankString(request.channelName(), "channelName");
//        ValidationMethods.validateNullBlankString(request.channelDescription(), "channelDescription");

        Channel channel = new Channel(owner, ChannelType.PUBLIC, request.channelName(), request.channelDescription());

        // owner는 channel 생성 시 자동 join(channel의 member list에도 추가
        linkMemberAndChannel(owner, channel);
        ReadStatus participantReadStatus = new ReadStatus(owner.getId(), channel.getId());

        channelRepository.save(channel);
        // `linkMemberAndChannel` 메소드로 owner(user)의 joinChannelList에 해당 channel 추가 후, 저장
        readStatusRepository.save(participantReadStatus);
        userRepository.save(owner);

        return channel;
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User owner = validateAndGetUserByUserId(request.ownerId());
        // PRIVATE 채널은 channelName과 channelDescription이 null
        Channel channel = new Channel(owner, ChannelType.PRIVATE, null, null);

        if (request.participantIds() != null && !request.participantIds().isEmpty()) {
            for (UUID participantId : request.participantIds()) {
                User participant = validateAndGetUserByUserId(participantId);
                linkMemberAndChannel(participant, channel);
                ReadStatus participantReadStatus = new ReadStatus(participant.getId(), channel.getId());

                userRepository.save(participant);
                readStatusRepository.save(participantReadStatus);
            }
        }
        // owner는 channel 생성 시 자동 join(channel의 member list에도 추가
        linkMemberAndChannel(owner, channel);
        ReadStatus ownerReadStatus = new ReadStatus(owner.getId(), channel.getId());

        channelRepository.save(channel);
        readStatusRepository.save(ownerReadStatus);
        // `linkMemberAndChannel` 메소드로 owner(user)의 joinChannelList에 해당 channel 추가 후, 저장
        userRepository.save(owner);

        return channel;
    }

    @Override
    public ChannelResponse findChannelById(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        Instant lastMessageTime = messageRepository.findByChannelId(channelId).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .map(message -> message.getCreatedAt())
                .orElse(null);

        List<UUID> channelMembersIds = new ArrayList<>();
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            channelMembersIds = channel.getChannelMembersList().stream()
                    .map(user -> user.getId())
                    .toList();
        }

        return createChannelPublicResponse(channel, lastMessageTime, channelMembersIds);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // User ID null 검증
        validateUserByUserId(userId);

        // 유저가 참여한 모든 채널
        List<UUID> participatedChannelList = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannelId())
                .toList();

        // 모든 채널에서 PUBLIC인 채널 전체와 유저가 참여한 모든 채널 비교?
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC || participatedChannelList.contains(channel.getId()))
                .map(channel -> findChannelById(channel.getId()))
                .toList();
    }

    @Override
    public Channel updateChannelInfo(ChannelUpdateRequest request) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        validateUserByUserId(request.ownerId());

//        ValidationMethods.validateId(request.channelId());

        // channel 객체 존재 확인
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        // PRIVATE Channel일 경우 수정 불가
        if (ChannelType.PRIVATE.equals(channel.getChannelType())) {
            throw new IllegalStateException("PRIVATE인 Channel은 수정 불가능합니다.");
        }

        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, request.ownerId());

        // `*Controller` 만들면 삭제
//        validateBlankUpdateParameters(request);

        // channelType, channelName, channelDescription이 전부 입력되지 않았거나, 전부 이전과 동일하다면 exception
        validateAllInputDuplicateOrEmpty(request, channel);

        Optional.ofNullable(request.channelName())
                .filter(n -> !channel.getChannelName().equals(n))
                .ifPresent(n -> channel.updateChannelName(n));
        Optional.ofNullable(request.channelDescription())
                .filter(d -> !channel.getChannelDescription().equals(d))
                .ifPresent(d -> channel.updateChannelDescription(d));

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel changeChannelOwner(UUID currentUserId, UUID channelId, UUID newOwnerId) {
        // Channel ID null & channel 객체 존재 확인
        ValidationMethods.validateId(channelId);
        Channel channel = validateAndGetChannelByChannelId(channelId);

        validateUserByUserId(currentUserId);
        User newOwner = validateAndGetUserByUserId(newOwnerId);
        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, currentUserId);

        // 새롭게 owner가 된 newOwner가 해당 채널에 이미 참여한 것인지 검증
        if (!channel.getChannelMembersList().stream()
                .anyMatch(user -> user.getId().equals(newOwnerId))) {
            throw new IllegalStateException("참여하지 않은 채널입니다. 먼저 채널에 참가하세요.");
        }

        // newOwner가 이미 owner인지 확인
        if (channel.getOwner().getId().equals(newOwnerId)) {
            throw new IllegalStateException("이미 owner인 채널입니다.");
        }

        // 변경
        channel.changeOwner(newOwner);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel joinChannel(UUID userId, UUID channelId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);
        // Channel ID null & channel 객체 존재 확인
        ValidationMethods.validateId(channelId);
        Channel channel = validateAndGetChannelByChannelId(channelId);

        // 이미 참여한 채널인지 검증
        if (channel.getChannelMembersList().stream()
                .anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("이미 참여한 채널입니다.");
        }

        linkMemberAndChannel(user, channel);
        ReadStatus readStatus = new ReadStatus(userId, channelId);

        channelRepository.save(channel);
        // `linkMemberAndChannel` 메소드로 user의 joinChannelList에 해당 channel 추가 후, 저장
        userRepository.save(user);
        readStatusRepository.save(readStatus);

        return channel;
    }

    @Override
    public Channel leaveChannel(UUID userId, UUID channelId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);
        // Channel ID null & channel 객체 존재 확인
        ValidationMethods.validateId(channelId);
        Channel channel = validateAndGetChannelByChannelId(channelId);

        // 참여한 채널인지 확인
        if (!channel.getChannelMembersList().stream()
                .anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("참여하지 않은 채널입니다. 참여하지 않았으므로 나갈 수도 없습니다.");
        }

        // 나가려는 채널의 owner인지 확인
        if (channel.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("이미 owner인 채널입니다. owner를 변경하고 채널에 나갈 수 있습니다.");
        }

        unlinkMemberAndChannel(user, channel);
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new NoSuchElementException("userId와 channelId에 해당하는 readStatus가 없습니다."));

        channelRepository.save(channel);
        // `unlinkMemberAndChannel` 메소드로 user의 joinChannelList에 해당 channel 삭제 후, 저장
        userRepository.save(user);
        readStatusRepository.delete(readStatus.getId());

        return channel;
    }

    @Override
    public void deleteChannel(UUID ownerId, UUID channelId) {
        // 로그인 되어있는 owner ID null & user 객체 존재 확인
        validateUserByUserId(ownerId);

        // Channel ID null & channel 객체 존재 확인
        ValidationMethods.validateId(channelId);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, ownerId);

        for (Message message : messageRepository.findByChannelId(channelId)) {
            User author = validateAndGetUserByUserId(message.getAuthor().getId());
            unlinkMessage(author, channel, message);

            if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
                for (UUID attachmentId : message.getAttachmentIds()) {
                    binaryContentRepository.delete(attachmentId);
                }
            }
            messageRepository.delete(message.getId());
            userRepository.save(author);
        }
        for (ReadStatus readStatus : readStatusRepository.findAllByChannelId(channelId)) {
            readStatusRepository.delete(readStatus.getId());
        }

        // 참여했던 user 객체에서 해당 channel Id 삭제하기
        for (User member : channel.getChannelMembersList()) {
            unlinkMemberAndChannel(member, channel);
            // `unlinkMemberAndChannel` 메소드로 member들의 joinChannelList에 해당 channel 삭제 후, 저장
            userRepository.save(member);
        }
        channelRepository.delete(channelId);
    }
    private void linkMemberAndChannel(User user, Channel channel) {
        channel.addMember(user);
        user.joinChannel(channel);
    }
    private void unlinkMemberAndChannel(User user, Channel channel) {
        user.leaveChannel(channel.getId());
        channel.removeMember(user.getId());
    }
    public void unlinkMessage(User author, Channel channel, Message message) {
        // author(user)의 writeMessageList에 저장된 message 객체 삭제
        author.removeUserMessage(message.getId());
        // channel의 channelMessagesList에 저장된 message 객체 삭제
        channel.removeMessageInChannel(message.getId());
    }
    private ChannelResponse createChannelPublicResponse(Channel channel, Instant lastMessageTime, List<UUID> channelMembersIds) {
        return new ChannelResponse(channel.getId(), channel.getOwner().getId(), channel.getChannelType(),
                channel.getChannelName(), channel.getChannelDescription(), channelMembersIds, lastMessageTime);
    }

    // validation
    //로그인 되어있는 user ID null & user 객체 존재 확인
    public void validateUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    public User validateAndGetUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    public Channel validateAndGetChannelByChannelId(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }
    // `*Controller` 만들면 삭제
//    private void validateBlankUpdateParameters(ChannelUpdateRequest request) {
//        if (request.channelName() != null) ValidationMethods.validateNullBlankString(request.channelName(), "channelName");
//        if (request.channelDescription() != null) ValidationMethods.validateNullBlankString(request.channelDescription(), "channelDescription");
//    }
    // channelType, channelName, channelDescription이 전부 입력되지 않았거나, 전부 이전과 동일하다면 exception
    private void validateAllInputDuplicateOrEmpty(ChannelUpdateRequest request, Channel channel) {
        if ((request.channelName() == null || channel.getChannelName().equals(request.channelName()))
                && (request.channelDescription() == null || channel.getChannelDescription().equals(request.channelDescription()))) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }
    }
    // channel owner의 user ID와 owner의 user ID가 동일한지 확인
    public void verifyChannelOwner(Channel channel, UUID ownerId) {
        // channel owner의 user Id와 owner의 user Id 동일한지 확인
        if (!channel.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("채널 owner만 수행 가능합니다.");
        }
    }
}
