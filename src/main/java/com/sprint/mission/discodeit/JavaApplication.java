package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        JCFUserService userdata = JCFUserService.getInstance();
        JCFChannelService channeldata = JCFChannelService.getInstance();
        JCFMessageService messagedata = JCFMessageService.getInstance();

        User user1 = userdata.create("초시");
        User user2 = userdata.create("초코");

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Message mes1 = messagedata.create(user1.getId(), "이게 뭐지", ch1.getId());
        Message mes2 = messagedata.create(user2.getId(), "그게 뭔데", ch1.getId());

        //불러오기

        System.out.println(messagedata.find(mes1.getId()));
        messagedata.findAll().stream().forEach(System.out::println);

        System.out.println(channeldata.find(ch2.getId()));
        channeldata.findAll().stream().forEach(System.out::println);

        System.out.println(userdata.find(user2.getId()));
        userdata.findAll().stream().forEach(System.out::println);

        System.out.println();

        //수정하기&조회
        messagedata.update(mes1.getId(), "이게 대체 뭐지?");
        System.out.println(messagedata.find(mes1.getId()));

        userdata.update(user2.getId(), "초코유");
        messagedata.findAll().stream().forEach(System.out::println);

        channeldata.update(ch1.getId(), null, "코딩 하는 채널");
        channeldata.findAll().stream().forEach(System.out::println);

        System.out.println();
        //유저권한테스트

        Channel ch3 = channeldata.create("비밀 채널", "아무나 못 들어오는 채널");
        ch3.addAllowedUser(user1);
        Message ms3 = messagedata.create(user1.getId(), "몰루", ch3.getId());
        //Message ms4 = messagedata.create(user2.getId(), "몰?????????루", ch3.getId());//여기서버그남

        System.out.println();

        //삭제하기, 조회
        messagedata.delete(mes2.getId());
        messagedata.findAll().stream().forEach(System.out::println);

        userdata.delete(user2.getId());
        userdata.findAll().stream().forEach(System.out::println);

        channeldata.delete(ch2.getId());
        channeldata.findAll().stream().forEach(System.out::println);

        System.out.println();
        //예외처리테스트
        //System.out.println(channeldata.find(UUID.randomUUID()));
    }
}
