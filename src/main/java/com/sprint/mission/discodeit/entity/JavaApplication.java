package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.view.ChannelMessageView;
import com.sprint.mission.discodeit.view.ChannelView;

import java.util.ArrayList;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(channelService);

        // ===== 유저 생성 =====
        String[][] userData = {
                {"김코딩", "kim@test.com"},
                {"이코딩", "lee@test.com"},
                {"박코딩", "park@test.com"},
                {"최코딩", "choi@test.com"},
                {"정코딩", "jung@test.com"},
                {"안코딩", "ahn@test.com"}
        };

        List<User> users = new ArrayList<>();
        for (String[] data : userData) users.add(userService.createUser(data[0], data[1]));

        //유저 단건 출력
        System.out.println("[유저 출력(단건)]");
        User findUser = userService.findUser(users.get(0).getId());
        System.out.println(findUser);
        System.out.println("개인 식별자(Id): " + findUser.getId());
        System.out.println();

        //유저 다건 출력
        System.out.println("[유저 출력(다건)]");
        for(User allUser : userService.findAllUser()){
            System.out.println(allUser);
            System.out.println();
        }

        //유저 수정 후 출력
        System.out.println("[유저 출력(수정)]");
        User updateUser = userService.updateUser(users.get(1).getId(), "이코딩_수정", "lee_update@test.com");
        System.out.println(updateUser);
        System.out.println();

        //유저 삭제 후 출력
        System.out.println("[유저 출력(삭제)]");
        User deleteUser = userService.deleteUser(users.get(0).getId());

        for(User allUser : userService.findAllUser()){
            System.out.println(allUser);
            System.out.println();
        }



        // ===== 채널 생성 =====
        String[] channelData = {"채널1", "채널2", "채널3", "채널4"};
        List<Channel> channels = new ArrayList<>();
        for (String name : channelData) channels.add(channelService.createChannel(name));

        // ===== 채널 멤버 추가 =====
        channelService.memberAddChannel(
                channels.get(0).getId(),
                users.get(0).getId()
        );
        channelService.memberAddChannel(
                channels.get(0).getId(),
                users.get(1).getId()
        );
        channelService.memberAddChannel(
                channels.get(1).getId(),
                users.get(2).getId()
        );
        channelService.memberAddChannel(
                channels.get(2).getId(),
                users.get(3).getId()
        );
        channelService.memberAddChannel(
                channels.get(3).getId(),
                users.get(4).getId()
        );
        channelService.memberAddChannel(
                channels.get(1).getId(),
                users.get(5).getId()
        );

        System.out.println("[채널 출력(단건)]");
        Channel findOneChannel = channelService.findChannel(channels.get(0).getId());
        System.out.println(ChannelView.viewChannel(findOneChannel));
        System.out.println();

        //채널 다건 출력
        System.out.println("[채널 출력(다건)]");
        for(Channel allChannel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(allChannel));
            System.out.println();
        }

        //채널 이름 수정
        System.out.println("[채널 이름 수정]");
        Channel updateChannel = channelService.nameUpdateChannel(channels.get(1).getId(), "채널2_수정");
        System.out.println(ChannelView.viewChannel(updateChannel));
        System.out.println();

        //채널 삭제
        System.out.println("[채널 삭제]");
        Channel deleteChannel = channelService.deleteChannel(channels.get(0).getId());
        for(Channel allChannel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(allChannel));
            System.out.println();
        }
        //채널 단건 출력




        // ===== 메시지 작성 =====
        Message msg1 = messageService.createMessage(
                channels.get(0).getId(),
                users.get(0).getId(),
                "안녕하세요!"
        );
        Message msg2 = messageService.createMessage(
                channels.get(0).getId(),
                users.get(1).getId(),
                "반갑습니다!"
        );
        Message msg3 = messageService.createMessage(
                channels.get(1).getId(),
                users.get(2).getId(),
                "채널2 첫 메시지"
        );

        // ===== 채널 출력 + 메시지 출력 =====
        for (Channel channel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(channel));
            System.out.println(ChannelMessageView.viewMessage(channel, messageService));
            System.out.println();
        }

        // ===== 메시지 수정 =====
        messageService.updateMessage(msg2.getChannel().getId(), msg2.getId(), "수정된 메시지입니다!");

        // ===== 수정 후 메시지 출력 =====
        System.out.println("=== 메시지 수정 후 출력 ===");
        for (Channel channel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(channel));
            System.out.println(ChannelMessageView.viewMessage(channel, messageService));
            System.out.println();
        }

        // ===== 메시지 삭제 =====
        messageService.deleteMessage(msg1.getChannel().getId(), msg1.getId());

        System.out.println("=== 메시지 삭제 후 출력 ===");
        for (Channel channel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(channel));
            System.out.println(ChannelMessageView.viewMessage(channel, messageService));
            System.out.println();
        }






    }
}
