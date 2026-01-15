package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);
        ((JCFUserService)userService).setMessageService(messageService);
        ((JCFUserService)userService).setChannelService(channelService);
        ((JCFChannelService)channelService).setMessageService(messageService);

        // --- User ---
        System.out.println("-- User --");
        System.out.println("<생성>");
        userService.createUser("AAA", "aaa", "A씨", "AAA@gmail.com");
        userService.createUser("BBB", "bbb", "B씨", "BBB@naver.com");
        userService.createUser("CCC", "ccc", "C씨", "CCC@icloud.com");


        System.out.println("<조회>");
        System.out.print("  단건: ");
        userService.findUserByAccountId("AAA").ifPresent(System.out::println);
        System.out.print("  다건: ");
        System.out.println(userService.findAllUsers().stream().toList());

        System.out.println("<수정>");
        userService.findUserByAccountId("AAA")
                .ifPresent(u-> userService.updateUser(u.getId(), null, "q1w2e3!", "A말씨", "aaa@outlook.com"));
        userService.findUserByAccountId("BBB")
                .ifPresent(u-> userService.updateUser(u.getId(), null, "bbb", "B명씨", "bbb@hanmail.net"));
        userService.findUserByAccountId("CCC")
                .ifPresent(u-> userService.updateUser(u.getId(), null, "ccc", "CC씨", "seamonkey@base.net"));
        System.out.print("  다건: ");
        System.out.println(userService.findAllUsers().stream().toList());


        System.out.println("<삭제>");
        userService.findUserByAccountId("AAA")
                .ifPresent(u-> userService.deleteUser(u.getId()));
        System.out.print("  다건: ");
        System.out.println(userService.findAllUsers().stream().toList());
        System.out.println("\n\n");


        // --- Channel ---
        System.out.println("-- Channel --");
        System.out.println("<생성>");
        channelService.createChannel("멍때리는 방", "머어어어엉~");
        channelService.createChannel("잠자는 방", "채팅시 강퇴");
        channelService.createChannel("두쫀쿠 헌터방", "위치 제보받아요");


        System.out.println("<조회>");
        System.out.print("  단건: ");
        channelService.findChannelByTitle("멍때리는 방").ifPresent(System.out::println);
        System.out.print("  다건: ");
        System.out.println(channelService.findAllChannels().stream().toList());

        System.out.println("<수정>");
        channelService.findChannelByTitle("잠자는 방")
                .ifPresent(c-> channelService.updateChannel(c.getId(), "러닝맨", "아침 6시부터 달려요"));
        System.out.print("  다건: ");
        System.out.println(channelService.findAllChannels().stream().toList());

        System.out.println("<삭제>");
        channelService.findChannelByTitle("멍때리는 방")
                .ifPresent(u-> channelService.deleteChannel(u.getId()));
        System.out.print("  다건: ");
        System.out.println(channelService.findAllChannels().stream().toList());

        System.out.println("<채널참여>");
        channelService.findChannelByTitle("러닝맨").ifPresent(c -> {
            User user = userService.findUserByAccountId("BBB").orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
            channelService.joinChannel(c.getId(), user.getId());
        });
        System.out.print("  참여자들 조회: ");
        channelService.findChannelByTitle("러닝맨").ifPresent(c -> System.out.println(c.getParticipants()));
        System.out.println("<채널나감>");
        channelService.findChannelByTitle("러닝맨").ifPresent(c -> {
            User user = userService.findUserByAccountId("BBB").orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
            channelService.leaveChannel(c.getId(), user.getId());
        });
        System.out.print("  참여자 빼고 다시 조회: ");
        channelService.findChannelByTitle("러닝맨").ifPresent(c -> System.out.println(c.getParticipants()));
        System.out.println("\n\n");

        // --- Message ---
        System.out.println("-- Message --");
        System.out.println("<생성>");
        User user = userService.findUserByAccountId("CCC").orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        Channel channel1 = channelService.findChannelByTitle("러닝맨").orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        Channel channel2 = channelService.findChannelByTitle("두쫀쿠 헌터방").orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));

        Message msg1 = messageService.createMessage(channel1.getId(), user.getId(), "러닝맨 여러분들 안녕하세요");
        Message msg2 = messageService.createMessage(channel2.getId(), user.getId(), "판매위치 제보받아요");
        System.out.println("<조회>");
        System.out.println("  단건: ");
        System.out.printf("\t%s\n", messageService.getMessage(msg1.getId()));
        System.out.println("  다건 조회: ");
        System.out.printf("\t%s\n",messageService.findAllMessages());
        System.out.println("  채널별 조회: ");
        System.out.printf("\t%s\n", messageService.findMessagesByChannelId(channel1.getId()));
        System.out.printf("\t%s\n", messageService.findMessagesByChannelId(channel2.getId()));


        System.out.println("<수정>");
        messageService.updateMessage(msg2.getId(), "판매위치 제보 받았습니다!!!!!!!!");
        System.out.print("  수정한 채널조회: ");
        System.out.println(messageService.findMessagesByChannelId(channel2.getId()));

        System.out.println("<삭제>");
        System.out.print("  삭제전 채널조회: ");
        System.out.println(messageService.findMessagesByChannelId(channel1.getId()));
        messageService.deleteMessage(msg1.getId());
        System.out.print("  삭제한 채널조회: ");
        System.out.println(messageService.findMessagesByChannelId(channel1.getId()));
    }
}
