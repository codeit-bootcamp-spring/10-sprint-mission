package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;

import java.text.SimpleDateFormat;

public class JavaApplication {
    public static void main(String[] args) {
        User user1 = new User("user1", "password1", "");
        User user2 = new User("user2", "password2", "");
        Channel channel = new Channel("channel1", ChannelType.PUBLIC, "");

        System.out.println("-".repeat(10));
        System.out.println("닉네임 변경전 time : " + miliToTime(user1.getUpdateAt()));
        user1.updateUserName("name1_changed");
        System.out.println("닉네임 변경후 time : " + miliToTime(user1.getUpdateAt()));
        System.out.println("-".repeat(10));

        System.out.println("변경전 채널타입 : " + channel.getChannelType());
        channel.updateChannelType();
        System.out.println("변경후 채널타입 : " + channel.getChannelType());

    }

    private static String miliToTime(long mili) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH시 mm분 ss초 SS");
        return sdf.format(mili);
    }
}