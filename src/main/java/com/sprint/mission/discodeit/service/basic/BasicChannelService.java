package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        Channel newChannel = new Channel(channelName, owner, channelType);
        channelRepository.save(newChannel);

        owner.addChannel(newChannel);
        userRepository.save(owner);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        return channelRepository.findById(targetChannelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 채널 다건 조회
    @Override
    public List<Channel> searchChannelAll() {
        return channelRepository.findAll();
    }

    // 특정 유저가 참가한 채널 리스트 조회
    public List<Channel> searchChannelsByUserId(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        List<Channel> channels = searchChannelAll();        // 함수가 실행된 시점에서 가장 최신 채널 목록

        return channels.stream()
                .filter(channel -> channel.getMembers().stream()
                        .anyMatch(member -> member.getId().equals(userId)))
                .toList();
    }

    // 채널 정보 수정
    @Override
    public Channel updateChannel(UUID targetChannelId, String newChannelName) {
        Channel targetChannel = searchChannel(targetChannelId);

        // 채널 이름 변경
        Optional.ofNullable(newChannelName)
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(newChannelName);
                });

        channelRepository.save(targetChannel);
        return targetChannel;
    }

    @Override
    public void updateChannel(UUID id, Channel channel) {}

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        targetChannel.getMembers().forEach(member -> {
            User targetUser = userRepository.findById(member.getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
            targetUser.getChannels().removeIf(channel -> channel.getId().equals(targetChannelId));
            userRepository.save(targetUser);
        });

        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = searchChannel(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        targetChannel.getMembers().add(newUser);
        channelRepository.save(targetChannel);

        newUser.addChannel(targetChannel);
        userRepository.save(newUser);
    }

    // 채널 퇴장
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = searchChannel(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().removeIf(member -> member.getId().equals(targetUserId));
        channelRepository.save(targetChannel);

        targetUser.getChannels().removeIf(channel -> channel.getId().equals(targetChannelId));
        userRepository.save(targetUser);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<User> currentMembers = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();

        if (currentMembers.stream().anyMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<User> currentMembers = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();

        if (currentMembers.stream().noneMatch(member -> member.getId().equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }
}
