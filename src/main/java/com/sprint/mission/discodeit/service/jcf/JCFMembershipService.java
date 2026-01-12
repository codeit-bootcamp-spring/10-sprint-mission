package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MembershipService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class JCFMembershipService implements MembershipService {

    private final UserService userService;
    private final ChannelService channelService;

    // 두 서비스를 생성자로 주입받음
    public JCFMembershipService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
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
            // 유저가 참여 중인 모든 채널에서 해당 유저 제거 (연쇄 작업)
            for (UUID channelId : user.getChannelIds()) {
                channelService.removeUser(channelId, userId);
            }
            // 최종적으로 유저 삭제
            userService.delete(userId);
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        if (channel != null) {
            // 채널에 참여 중인 모든 유저의 참여 목록에서 해당 채널 제거 (연쇄 작업)
            for (UUID userId : channel.getUserIds()) {
                User user = userService.findById(userId);
                if (user != null) user.leaveChannel(channelId);
            }
            // 최종적으로 채널 삭제
            channelService.delete(channelId);
        }
    }
}