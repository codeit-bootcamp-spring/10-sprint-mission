package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFChannelService implements ChannelService {
    //    private final Map<UUID, Channel> data; // channel DB 데이터
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    // C. 생성: Channel 생성 후 Channel 객체 반환
    @Override
    public Channel createChannel(UUID userId, ChannelType channelType, String channelName, String channelDescription) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User owner = userService.findUserById(userId);
        // channelType `null` 검증
        validateNullChannelType(channelType);
        // channelName과 channelDescription의 `null`, `blank` 검증
        ValidationMethods.validateNullBlankString(channelName, "channelName");
        ValidationMethods.validateNullBlankString(channelDescription, "channelDescription");

        Channel channel = new Channel(owner, channelType, channelName, channelDescription);

        // owner는 channel 생성 시 자동 join(channel의 member list에도 추가
        linkMemberAndChannel(owner, channel);
        channelRepository.save(channel);
        return channel;
    }

    // R. 읽기
    // 특정 채널 정보 읽기
    @Override
    public Channel findChannelById(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    // R. 모두 읽기
    // 채널 목록 전체
    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    // 비공개 여부에 따른 채널 목록
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

    // 해당 유저가 join한 모든 채널
    @Override
    public List<Channel> findJoinChannelsByUserId(UUID userId) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        User user = userService.findUserById(userId);

        return user.getJoinChannelList();
    }

    // 특정 사용자가 owner인 모든 채널
    @Override
    public List<Channel> findOwnerChannelsByUserId(UUID userId) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        userService.findUserById(userId);

        return findAllChannels().stream()
                .filter(channel -> channel.getOwner().getId().equals(userId))
                .toList();
    }

    // U. 수정
    // 로그인 정보를 가져온다고 가정하면 `requestUserId` 와 `targetUserId` 로 나눌 필요는 없음
    // ID null 검증 & req ID와 target ID의 동일한지 확인 & user 객체와 channel 객체 존재 확인
    @Override
    public Channel updateChannelInfo(UUID ownerId, UUID channelId, ChannelType channelType, String channelName, String channelDescription) {
        // 로그인 되어있는 user ID null & user 객체 존재 확인
        userService.findUserById(ownerId);
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

    // 채널 owner 변경
    @Override
    public Channel changeChannelOwner(UUID currentUserId, UUID channelId, UUID newOwnerId) {
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);
        userService.findUserById(currentUserId);
        User newOwner = userService.findUserById(newOwnerId);
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
//        newOwner.joinChannel(channel);
        return channel;
    }

    // 채널 참여하기
    @Override
    public Channel joinChannel(UUID userId, UUID channelId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = userService.findUserById(userId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);

        // 이미 참여한 채널인지 검증
        if (channel.getChannelMembersList().stream()
                .anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("이미 참여한 채널입니다.");
        }

        linkMemberAndChannel(user, channel);
        channelRepository.save(channel);
        return channel;
    }

    // 채널 나가기
    @Override
    public Channel leaveChannel(UUID userId, UUID channelId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = userService.findUserById(userId);
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
        return channel;
    }

    // D. 삭제
    @Override
    public void deleteChannel(UUID ownerId, UUID channelId) {
        // 로그인 되어있는 owner ID null & user 객체 존재 확인
        userService.findUserById(ownerId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = findChannelById(channelId);
        // channel owner의 user ID와 owner의 user ID가 동일한지 확인
        verifyChannelOwner(channel, ownerId);

        // 메세지 삭제는 상위에서 진행
        // 참여했던 user 객체에서 해당 channel Id 삭제하기
        for (User member : channel.getChannelMembersList()) {
            unlinkMemberAndChannel(member, channel);
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
