package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannel;
import com.sprint.mission.discodeit.service.jcf.JCFMessage;
import com.sprint.mission.discodeit.service.jcf.JCFUser;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        JCFUser userdata = JCFUser.getInstance();
        JCFChannel channeldata = JCFChannel.getInstance();
        JCFMessage messagedata = JCFMessage.getInstance();

        User user1 = new User("초시");
        User user2 = new User("초코");
        userdata.create(user1);
        userdata.create(user2);

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Message mes1 = new Message(user1, "이게 뭐지", ch1);
        Message mes2 = new Message(user2, "그게 먼데", ch1);
        messagedata.create(mes1);
        messagedata.create(mes2);

        //불러오기

        System.out.println(channeldata.find(user1.getId()));

        System.out.println(messagedata.find(mes1.getId()));
        messagedata.findAll().stream().forEach(System.out::println);

        System.out.println(channeldata.find(ch2.getId()));
        channeldata.findAll().stream().forEach(System.out::println);

        System.out.println(userdata.find(user2.getId()));
        userdata.findAll().stream().forEach(System.out::println);

        //수정하기&조회
        messagedata.update(mes1.getId(), "이게 대체 뭐지?");
        System.out.println(messagedata.find(mes1.getId()));

        userdata.update(user2.getId(), "초코유");
        messagedata.findAll().stream().forEach(System.out::println);

        channeldata.updateDesc(ch1.getId(), "코딩 하는 채널");
        channeldata.findAll().stream().forEach(System.out::println);

        //삭제하기, 조회
        messagedata.delete(mes2.getId());
        messagedata.findAll().stream().forEach(System.out::println);

        userdata.delete(user2.getId());
        userdata.findAll().stream().forEach(System.out::println);

        channeldata.delete(ch2.getId());
        channeldata.findAll().stream().forEach(System.out::println);

    }
}
