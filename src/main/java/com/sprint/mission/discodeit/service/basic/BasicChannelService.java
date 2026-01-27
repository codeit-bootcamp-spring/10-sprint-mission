package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel createChannel(UUID userId, ChannelType channelType, String channelName, String channelDescription) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User owner = validateAndGetUserByUserId(userId);
        // channelType `null` 검증
        validateNullChannelType(channelType);
        // channelName과 channelDescription의 `null`, `blank` 검증
        ValidationMethods.validateNullBlankString(channelName, "channelName");
        ValidationMethods.validateNullBlankString(channelDescription, "channelDescription");

        Channel channel = new Channel(owner, channelType, channelName, channelDescription);

        // owner는 channel 생성 시 자동 join(channel의 member list에도 추가
        linkMemberAndChannel(owner, channel);

        channelRepository.save(channel);
        // `linkMemberAndChannel` 메소드로 owner(user)의 joinChannelList에 해당 channel 추가 후, 저장
        userRepository.save(owner);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findPublicOrPrivateChannel(ChannelType channelType) {
        if (channelType == null) return findAllChannels();

        switch (channelType) {
            case PUBLIC -> {
                return findAllChannels().stream()
                        .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC)
                        .toList();
            }
            case PRIVATE -> {
                return findAllChannels().stream()
                        .filter(channel -> channel.getChannelType() == ChannelType.PRIVATE)
                        .toList();
            }
            default -> {
                return findAllChannels();
            }
        }
    }

    @Override
    public List<Channel> findJoinChannelsByUserId(UUID userId) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        return user.getJoinChannelList();
    }

    @Override
    public List<Channel> findOwnerChannelsByUserId(UUID userId) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        validateUserByUserId(userId);

        return findAllChannels().stream()
                .filter(channel -> channel.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Channel updateChannelInfo(UUID ownerId, UUID channelId, ChannelType channelType, String channelName, String channelDescription) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        validateUserByUserId(ownerId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);
        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, ownerId);
        // blank 검증
        if (channelName != null) ValidationMethods.validateNullBlankString(channelName, "channelName");
        if (channelDescription != null) ValidationMethods.validateNullBlankString(channelDescription, "channelDescription");

        // channelType, channelName, channelDescription이 전부 입력되지 않았거나, 전부 이전과 동일하다면 exception
        if ((channelType == null || channel.getChannelType().equals(channelType))
                && (channelName == null || channel.getChannelName().equals(channelName))
                && (channelDescription == null || channel.getChannelDescription().equals(channelDescription))) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }

        Optional.ofNullable(channelType)
                .filter(t -> !channel.getChannelType().equals(t))
                .ifPresent(t -> channel.updateChannelType(t));
        Optional.ofNullable(channelName)
                .filter(n -> !channel.getChannelName().equals(n))
                .ifPresent(n -> channel.updateChannelName(n));
        Optional.ofNullable(channelDescription)
                .filter(d -> !channel.getChannelDescription().equals(d))
                .ifPresent(d -> channel.updateChannelDescription(d));

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel changeChannelOwner(UUID currentUserId, UUID channelId, UUID newOwnerId) {
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);
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
        Channel channel = findChannelById(channelId);

        // 이미 참여한 채널인지 검증
        if (channel.getChannelMembersList().stream()
                .anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("이미 참여한 채널입니다.");
        }

        linkMemberAndChannel(user, channel);
        channelRepository.save(channel);
        // `linkMemberAndChannel` 메소드로 user의 joinChannelList에 해당 channel 추가 후, 저장
        userRepository.save(user);
        return channel;
    }

    @Override
    public Channel leaveChannel(UUID userId, UUID channelId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);

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
        channelRepository.save(channel);
        // `unlinkMemberAndChannel` 메소드로 user의 joinChannelList에 해당 channel 삭제 후, 저장
        userRepository.save(user);
        return channel;
    }

    @Override
    public void deleteChannel(UUID ownerId, UUID channelId) {
        // 로그인 되어있는 owner ID null & user 객체 존재 확인
        validateUserByUserId(ownerId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);
        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, ownerId);

        // 메세지 삭제는 상위에서 진행
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

    // validation
    //로그인 되어있는 user ID null & user 객체 존재 확인
    public void validateUserByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    public User validateAndGetUserByUserId(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    // ChannelType null 검증
    public void validateNullChannelType(ChannelType channelType) {
        String message = "channelType이 null 입니다.";
        Objects.requireNonNull(channelType, message);
    }

    // channel owner의 user ID와 owner의 user ID가 동일한지 확인
    public void verifyChannelOwner(Channel channel, UUID ownerId) {
        // channel owner의 user Id와 owner의 user Id 동일한지 확인
        if (!channel.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("채널 owner만 수행 가능합니다.");
        }
    }
}
