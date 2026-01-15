package com.sprint.mission.discodeit.view;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.stream.Collectors;

public class ChannelView {

    // 채널 출력
    public static String viewChannel(Channel channel, UserService userService) {
        String memberNames = channel.getMemberIds().stream()
                .map(userService::findUser)
                .map(User::getUserName)
                .collect(Collectors.joining(" , "));
        return "[" + channel.getChannelName() + "]\n" + memberNames;
    }
}
