package com.sprint.mission.discodeit.view;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.util.stream.Collectors;

public class ChannelView {
    public static String viewChannel(Channel channel) {
        String memberNames = channel.getChannelUser().stream()
                .map(User::getUserName)
                .collect(Collectors.joining(" , "));
        return "[" + channel.getChannelName() + "]\n" + memberNames;
    }
}
