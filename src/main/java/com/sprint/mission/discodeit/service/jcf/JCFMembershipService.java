package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MembershipService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMembershipService implements MembershipService {

    // 유저, 채널 JCF 객체 저장
    // 상호 의존 문제를 피하기 위해 채널 참가, 채널 나가기, 유저 삭제, 채널 삭제를 별도 서비스에서 구현
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFMembershipService(
            UserService userService,
            ChannelService channelService,
            MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public boolean isMember(UUID userId, UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return channel != null && channel.getUserIds().contains(userId);
    }

    @Override
    public void join(UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (user != null && channel != null) {
            user.joinChannel(channelId);     // User 객체 업데이트
            channel.addUser(userId);         // Channel 객체 업데이트
        }
    }

    @Override
    public void leave(UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (user != null && channel != null) {
            user.leaveChannel(channelId);    // User 객체 업데이트
            channel.removeUser(userId);      // Channel 객체 업데이트
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userService.findById(userId);
        if (user != null) {
            for (UUID channelId : user.getChannelIds()) {
                channelService.removeUser(channelId, userId);
            }
            userService.delete(userId);
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        if (channel != null) {
            List<Message> messagesToDelete = new ArrayList<>(messageService.findAllByChannelId(channelId));
            // 리스트 복사본을 통해 삭제
            for (Message m : messagesToDelete)
                messageService.delete(m.getId());

            for (UUID userId : channel.getUserIds()) {
                User user = userService.findById(userId);
                if (user != null) user.leaveChannel(channelId);
            }

            channelService.delete(channelId);
        }
    }
}