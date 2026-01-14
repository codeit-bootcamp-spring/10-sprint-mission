package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class JCFInteractionService implements InteractionService {

    // 유저, 채널 JCF 객체 저장
    // 상호 의존 문제를 피하기 위해 채널 참가, 채널 나가기, 유저 삭제, 채널 삭제를 별도 서비스에서 구현
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFInteractionService(
            UserService userService,
            ChannelService channelService,
            MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public void join(UUID userId, UUID channelId) {     // 유저가 채널에 참가
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");
        Objects.requireNonNull(channel, "채널 객체가 유효하지 않습니다.");

        user.joinChannel(channel);     // User 객체 업데이트
        channel.addUser(user);         // Channel 객체 업데이트

    }

    @Override
    public void leave(UUID userId, UUID channelId) {    // 유저가 채널 탈퇴
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");
        Objects.requireNonNull(channel, "채널 객체가 유효하지 않습니다.");

        user.leaveChannel(channel);    // User 객체 업데이트
        channel.removeUser(user);      // Channel 객체 업데이트

    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userService.findById(userId);
        Objects.requireNonNull(user, "유저 객체가 유효하지 않습니다.");
        for (Channel channel : user.getChannels())
            channel.removeUser(user);

        userService.delete(user.getId());

    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        Objects.requireNonNull(channel, "삭제할 채널 객체는 null일 수 없습니다.");
        List<Message> messagesToDelete = new ArrayList<>(messageService.findAllByChannelId(channel.getId()));
        // 리스트 복사본을 통해 삭제
        for (Message m : messagesToDelete)
            messageService.delete(m.getId());

        for (User user : channel.getUsers()) {
            user.leaveChannel(channel);
        }

        channelService.delete(channel.getId());
    }
}
