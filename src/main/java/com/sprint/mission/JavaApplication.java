package com.sprint.mission;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;
import com.sprint.mission.service.jcf.JCFChannelService;
import com.sprint.mission.service.jcf.JCFMessageService;
import com.sprint.mission.service.jcf.JCFUserService;

import java.util.List;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);

        // 유저 서비스 구현 테스트
        // 등록
        User user1 = userService.createUser("종혁");
        User user2 = userService.createUser("유리");
        User user3 = userService.createUser("이씨");
        User user4 = userService.createUser("오씨");
        User user5 = userService.createUser("서씨");
        User user6 = userService.createUser("황씨");
        User user7 = userService.createUser("홍씨");

        // 조회(단건)
        System.out.println(userService.findById(user1.getId()).getNickName());

        // 수정 및 수정 확인
        userService.updateUser(user1.getId(), "오종혁");
        System.out.println(user1.getNickName());

        // 조회(다건)
        List<User> users = userService.findAll();
        String result = users.stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result);

        // 삭제 및 삭제확인
        userService.deleteById(user1.getId());
        List<User> users2 = userService.findAll();
        String result2 = users2.stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result2);
        // ==================================================

        System.out.println("=".repeat(50));

        // ==================================================
        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
        // 채널 서비스 구현 테스트
        // 등록
        Channel channel1 = channelService.create(user2.getId(), "일반1");
        Channel channel2 = channelService.create(user3.getId(), "일반2");
        Channel channel3 = channelService.create(user6.getId(), "일반3");

        // 조회(단건)
        System.out.println(channelService.findById(channel1.getId()).getName());
        System.out.println("사용자 id: " + user2.getId());
        System.out.println("방장 id: " + channel1.getOwnerId());

        // 수정 및 수정 확인
        System.out.println("채널명 수정 전: " + channel1.getName());
        channel1.updateName("일반11");
        System.out.println("채널명 수정 후: " + channel1.getName());

        // 조회(다건)
        List<Channel> channels = channelService.findAll();
        String channelResult = channels.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "모든 채널 출력: [", "]"));
        System.out.println(channelResult);

        // 삭제 및 삭제확인
        List<Channel> beforeDeleteChannels1 = channelService.findAll();
        String deleteChannelsResult1 = beforeDeleteChannels1.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "채널 삭제 전 : [", "]"));
        System.out.println(deleteChannelsResult1);

        channelService.deleteById(channel1.getId()); // 채널1 삭제

        List<Channel> afterDeleteChannels2 = channelService.findAll();
        String deleteChannelsResult2 = afterDeleteChannels2.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "채널 삭제 후 : [", "]"));
        System.out.println(deleteChannelsResult2);

        // 채널에 유저 추가 및 확인
        Channel user4JoinChannel2 = channelService.joinChannel(user4.getId(), channel2.getId());
        System.out.println("user4가 참여한 채널 id: " + user4JoinChannel2.getId());
        boolean participated1 = user4.getChannelIds().stream()
                .anyMatch(channelId -> channelId.equals(user4JoinChannel2.getId()));
        if (participated1) {
            System.out.println("user4는 " + user4JoinChannel2.getId() + "에 참가하고 있습니다.");
        }

        Channel user5JoinChannel2 = channelService.joinChannel(user5.getId(), channel2.getId());
        System.out.println("user5가 참여한 채널 id: " + user5JoinChannel2.getId());
        boolean participated2 = user5.getChannelIds().stream()
                .anyMatch(channelId -> channelId.equals(user5JoinChannel2.getId()));
        if (participated2) {
            System.out.println("user5는 " + user5JoinChannel2.getId() + "에 참가하고 있습니다.");
        }

        // 예시용 데이터 추가
        channelService.joinChannel(user7.getId(), channel3.getId());

        // 유저5의 채널2 나가기 및 확인 (양방향 체크)
        Channel user5LeaveChannel2 = channelService.leaveChannel(user5.getId(), user4JoinChannel2.getId());

        // 채널2 안에 유저5가 없는지
        boolean isUser5StillIn = user5LeaveChannel2.getUsers().stream()
                .anyMatch(userId -> userId.equals(user5.getId()));
        if (!isUser5StillIn) {
            System.out.printf("채널 id: %s에 유저 id: %s가 존재하지 않습니다.%n",
                    user5LeaveChannel2.getId(), user5.getId());
        }
        // 유저5가 속했던 채널2 중 방금 나갔던 채널2가 없는지
        boolean channelRemovedFromUser = user5.getChannelIds()
                .stream()
                .noneMatch(channelId -> channelId.equals(user5LeaveChannel2.getId()));
        if (channelRemovedFromUser) {
            System.out.printf("유저 id: %s 의 채널 목록에는 채널 id: %s가 존재하지 않습니다.%n",
                    user5.getId(), user5LeaveChannel2.getId());
        }


        // ==================================================

        System.out.println("=".repeat(50));

        // ==================================================

        // 메시지 서비스 구현 테스트
        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
        // 현재 살아있는 채널(일반2: 이씨(방장), 일반3: 황씨(방장), 홍씨)
        // 등록
        Message message = messageService.createMessage(user3.getId(), channel2.getId(), "안녕하세요");
        Message message2 = messageService.createMessage(user3.getId(), channel2.getId(), "이씨입니다.");
        Message message3 = messageService.createMessage(user6.getId(), channel3.getId(), "반갑습니다");
        Message message4 = messageService.createMessage(user6.getId(), channel3.getId(), "황씨입니다.");

        // 조회(단건)
        System.out.println("메시지 id로 조회: " + messageService.findById(message.getId()).getContent());

        // 수정 및 수정 확인
        System.out.println("메시지 수정 전: " + message.getContent());
        messageService.updateMessage(message.getUserId(), message.getId(), "안녕하세요 !!");
        System.out.println("메시지 수정 후: " + message.getContent());

        // 조회(다건)
        List<Message> messages = messageService.findAll();
        messages.forEach(m -> System.out.println("모든 채널의 메시지 출력: " + m.getContent()));

        // 조회(다건) 해당 채널 안의 메시지 표시
        List<Message> channelInMessage = messageService.findByChannelId(channel2.getId());
        channelInMessage.forEach(m -> System.out.println("채널 안 메시지: " + m.getContent()));

        // 조회(다건) 해당 채널 안의 유저가 남긴 메시지 표시
        List<Message> channelInUserMessage = messageService.findByUserIdAndChannelId(user3.getId(), channel2.getId());
        channelInMessage.forEach(m -> System.out.println("채널 안 유저 메시지: " + m.getContent()));

        // 삭제 및 삭제확인
        List<Message> originalMessages = messageService.findAll();
        String originalMessagesResult = originalMessages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(", ", "메시지 삭제 전: [", "]"));
        System.out.println(originalMessagesResult);

        messageService.deleteById(user3.getId(), message.getId()); // 안녕하세요 삭제

        List<Message> deleteMessages = messageService.findAll();
        String deleteMessagesResult = deleteMessages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(", ", "메시지 삭제 후: [", "]"));
        System.out.println(deleteMessagesResult);

        // ==================================================

        System.out.println("=".repeat(50));
    }
}