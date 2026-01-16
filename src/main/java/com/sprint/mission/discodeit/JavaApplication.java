package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("========== 테스트 시작 ==========");
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);

        System.out.println("========== 1. 등록 및 조회 ==========");
        System.out.println("========== 1-1. user1, user2 생성(ex: 회원가입 후 로그인 같은 느낌?) ==========");

        User user1 = userService.createUser("user1@email,com", "[user1]Name", "user1Nick", "1111", "20001111");
        User user2 = userService.createUser("user2@email,com", "[user2]Name", "user2Nick", "2222", "20002222");
        System.out.println("[user1] = " + userService.findUserById(user1.getId()).map(user -> user.getId()) + ", [user2] = " + userService.findUserById(user2.getId()).map(user -> user.getId()));

        System.out.println("\n========== 1-3. 전체 유저 조회 ==========");
        System.out.println("[모든 user] = " + userService.findAllUsers().stream().map(user -> user.getId()).toList());


        System.out.println("\n========== 1-4. channel1, channel2 생성 ==========");
        Channel channel1 = channelService.createChannel(user1.getId(), ChannelType.PUBLIC, "[channel1]", "channel1");
        Channel channel2 = channelService.createChannel(user2.getId(), ChannelType.PRIVATE, "[channel2]", "channel2");
        System.out.println("[channel1] = " + channelService.findChannelById(channel1.getId()).map(channel -> channel.getId()) + ", [channel2] = " + channelService.findChannelById(channel2.getId()).map(channel -> channel.getId()));
        System.out.println("[channel1_Owner = " + userService.findUserById(channel1.getOwnerId()).map(user -> user.getId()) + ", [channel2_Owner] = " + userService.findUserById(channel2.getOwnerId()).map(user -> user.getId()));
        System.out.println("[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());

        System.out.println("\n========== 1-5. user1이 channel2에 참여 ==========");
        System.out.println("(전)[channel2의 참여자 정보]" + joinMembers(userService, channelService, channel2.getId()));
        System.out.println("(전)[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());
        System.out.println("(전)[channel2 참여자들] = " + channelService.findMemberIdsByChannelId(channel2.getId()));
        channelService.joinChannel(user1.getId(), channel2.getId());
        System.out.println("(후)channel2 참여자들 = " + channelService.findMemberIdsByChannelId(channel2.getId()));
        System.out.println("[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());
        System.out.println("(후)[channel2의 참여자 정보]" + joinMembers(userService, channelService, channel2.getId()));

        System.out.println("\n========== 1-6. message1, message2, message3 생성 ==========");
        Message message1 = messageService.createMessage(channel1.getId(), user1.getId(), "channel1user1[message1]");
        Message message2 = messageService.createMessage(channel2.getId(), user1.getId(), "channel1user1[message2]");
        Message message3 = messageService.createMessage(channel2.getId(), user2.getId(), "channel2user2[message3]");
        try {
            // 참가하지 않은 채널에 메세지 보내기
            Message message4 = messageService.createMessage(channel1.getId(), user2.getId(), "channel2user1[message3]");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        System.out.println("[message1] = " + message1.getId() + ", [message2] = " + message2.getId() + ", [message3] = " + message3.getId());
        System.out.println("(후)user1의 message list(message1,2) = " + messageService.findUserMessagesByUserId(user1.getId()).stream().map(message -> message.getId()).toList());
        System.out.println("(후)channel2의 모든 message = " + messageService.findChannelMessagesByChannelId(channel2.getId()).stream().map(message -> message.getId()).toList());
        System.out.println("(후)channel2의 모든 message 정보 = " + messageService.findChannelMessagesByChannelId(channel2.getId()));


        System.out.println("\n========== 2. 수정 및 수정된 데이터 조회 ==========");
        System.out.println("========== 2-1. user1의 email, userName 수정 후 조회 ==========");
        System.out.println("(전)user1 userName = " + userService.findUserById(user1.getId())); // [user1]Name

        try {
            // 전부 동일한 값
            userService.updateUserInfo(user1.getId(), "user1@email,com", "1111", "[user1]Name", "user1Nick", "20001111");
            System.out.println("(후)user1 userName = " + userService.findUserById(user1.getId())); // [update]user1Name
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        userService.updateUserInfo(user1.getId(), "change", null, "[update]user1Name", null, null);
        System.out.println("(후)user1 userName = " + userService.findUserById(user1.getId())); // [update]user1Name

        System.out.println("\n========== 2-2. channel1의 channelName 수정 후 조회 ==========");
        System.out.println("(전)channel1 channelName = " + channelService.findChannelById(channel1.getId()));
        try {
            // 전부 동일한 값
            channelService.updateChannelInfo(user1.getId(), channel1.getId(), ChannelType.PUBLIC,"[channel1]", "channel1");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        channelService.updateChannelInfo(user1.getId(), channel1.getId(), ChannelType.PUBLIC,"[update]channel1Name", "channel1");
        System.out.println("(후)channel1 channelName = " + channelService.findChannelById(channel1.getId()));
        System.out.println("(후)user1이 참여한 channelName 리스트 = " + userService.findUserById(user1.getId()).get().getJoinChannelIds());

        System.out.println("\n========== 2-3. channel2의 owner(user2)를 user1으로 변경 후 조회 ==========");
        System.out.println("(전)[user2의 owner 정보 list]" + ownerChannelList(userService, channelService, user2.getId()));
        System.out.println("(전)[user1의 owner 정보 list]" + ownerChannelList(userService, channelService, user1.getId()));
        System.out.println("(전)[channel2 id] = " + channelService.findChannelById(channel2.getId()).get().getId());
        System.out.println("(전)[user1 id] = " + userService.findUserById(user1.getId()).get().getId() + ", [user2 id] = " + userService.findUserById(user2.getId()).get().getId() + ", [channel2 owner] = " + channelService.findChannelById(channel2.getId()).get().getOwnerId());
        System.out.println("(전)[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());
        System.out.println("(전)[user1가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user1.getId()).stream().map(channel -> channel.getId()).toList());
        System.out.println("(전)[user2가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user2.getId()).stream().map(channel -> channel.getId()).toList());
        System.out.println(channelService.findOwnerChannelsByUserId(user2.getId()).stream().anyMatch(c -> c.getOwnerId().equals(user1.getId())));
        channelService.changeChannelOwner(user2.getId(), channel2.getId(), user1.getId());
        System.out.println("(후)[user1의 owner list]" + ownerChannelList(userService, channelService, user1.getId()));
        System.out.println("(후)[user1가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user1.getId()).stream().map(channel -> channel.getId()).toList());
        System.out.println("(후)[user2가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user2.getId()).stream().map(channel -> channel.getId()).toList());

        System.out.println("-----원상 복귀-----");
        channelService.changeChannelOwner(user1.getId(), channel2.getId(), user2.getId());
        System.out.println("[user1가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user1.getId()).stream().map(channel -> channel.getId()).toList());
        System.out.println("[user2가 owner인 channel] = " + channelService.findOwnerChannelsByUserId(user2.getId()).stream().map(channel -> channel.getId()).toList());

        System.out.println("\n========== 2-4. channel2(owner는 user2)에서 user1 떠나기 ==========");
        System.out.println("(전)[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());
        System.out.println("(전)channel2 참여자들 = " + channelService.findMemberIdsByChannelId(channel2.getId()));
        try {
            // user2가 참여하지 않은 channel1에서 나가려고 할 때
            channelService.leaveChannel(user2.getId(), channel1.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        try {
            // user1이 owner인 channel1에서 owner를 변경하지 않고 나가려고 할 때
            channelService.leaveChannel(user1.getId(), channel1.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        channelService.leaveChannel(user1.getId(), channel2.getId());
        System.out.println("(후)[user1_joinChannelList] = " + user1.getJoinChannelIds() + ", [user2_joinChannelList] = " + user2.getJoinChannelIds());
        System.out.println("(후)channel2 참여자들 = " + channelService.findMemberIdsByChannelId(channel2.getId()));

        System.out.println("\n========== 2-5. 공개&비공개 channel 목록 변경 ==========");
        System.out.println("(전)[PUBLIC channel 목록] = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC));
        System.out.println("(전)[PRIVATE channel 목록] = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE));
        channelService.updateChannelInfo(user1.getId(), channel1.getId(), ChannelType.PRIVATE, null, null);
        System.out.println("(후)[PUBLIC channel 목록] = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC));
        System.out.println("(후)[PRIVATE channel 목록] = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE));

        System.out.println("\n========== 2-6. message2의 content 수정 후 조회 ==========");
        System.out.println("(전)[message2 content] = " + message2.getMessageContent());
        messageService.updateMessageContent(user1.getId(), message2.getId(), "[update]channel1user1[message2]");
        try {
            messageService.updateMessageContent(user1.getId(), message3.getId(), "[update]channel1user1[message2]");
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        System.out.println("(후)[message2 content] = " + message2.getMessageContent());
        System.out.println("(후)[message3 content] = " + message3.getMessageContent());

//        channelService.leaveChannel(user1.getId(), channel2.getId());

        System.out.println("\n========== 3. 삭제 및 삭제 후 데이터 조회 ==========");
        System.out.println("========== 3-1. message2 삭제 및 삭제 후 데이터 조회 ==========");

        System.out.println("[message1 id] = " + message1.getId() + ", [message2 id] = " + message2.getId() + ", [message3 id] = " + message3.getId());
        System.out.println("(전)[channel2의 message list] = " + messageService.findChannelMessagesByChannelId(channel1.getId()));
        System.out.println("(전)[user1의 message list] = " + messageService.findUserMessagesByUserId(user1.getId()));
        messageService.deleteMessage(user1.getId(), message2.getId());
        message2 = null;
        System.out.println("(후)[channel2의 message list] = " + messageService.findChannelMessagesByChannelId(channel1.getId()));
        System.out.println("(후)[user1의 message list] = " + messageService.findUserMessagesByUserId(user1.getId()));
        try {
            // 작성자가 아닌 사용자가 삭제 시도 시
            messageService.deleteMessage(user1.getId(), message3.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        channelService.joinChannel(user2.getId(), channel1.getId());
        Message message5 = messageService.createMessage(channel1.getId(), user2.getId(), "channel1user2[message5]");
        System.out.println("\n========== 3-2. channel1 삭제 및 삭제 후 데이터 조회 ==========");
        System.out.println("(전)전체 채널 리스트 = " + channelService.findAllChannels());
        System.out.println("(전)[channel1의 message list] = " + messageService.findChannelMessagesByChannelId(channel1.getId()));
        System.out.println("(전)[user1의 join channel list] = " + userService.findJoinChannelIdsByUserId(user1.getId()));
        System.out.println("(전)[user2의 join channel list] = " + userService.findJoinChannelIdsByUserId(user2.getId()));
        System.out.println("(전)[user1의 message list] = " + messageService.findUserMessagesByUserId(user1.getId()));
        System.out.println("(전)[user2의 message list] = " + messageService.findUserMessagesByUserId(user2.getId()));
        System.out.println("===== [채널 삭제 시작] =====");
        try {
            deleteChannel(user1.getId(), channel1.getId(), channelService, messageService);
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println(e);
        }
        System.out.println("===== [채널 삭제 종료] =====");
        try {
            deleteChannel(user1.getId(), channel1.getId(), channelService, messageService);
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println(e);
        }
        System.out.println("(후)전체 채널 리스트 = " + channelService.findAllChannels());
        System.out.println("(후)[user1의 join channel list] = " + userService.findJoinChannelIdsByUserId(user1.getId()));
        System.out.println("(후)[user2의 join channel list] = " + userService.findJoinChannelIdsByUserId(user2.getId()));
        System.out.println("(후)[user1의 message list] = " + messageService.findUserMessagesByUserId(user1.getId())); // 메세지 미삭제 시 남아있음
        System.out.println("(후)[user2의 message list] = " + messageService.findUserMessagesByUserId(user2.getId())); // 메세지 미삭제 시 남아있음

        System.out.println("\n========== 3-3. user2 삭제 및 삭제 후 데이터 조회 ==========");
        System.out.println("===== owner 미 변경하고 삭제 시작 =====");
        try {
            deleteUser(user2.getId(), userService, channelService, messageService);
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println(e);
        }
        System.out.println("===== owner 미 변경하고 삭제 종료 =====");
        // 원활한 삭제를 위해 owner 변경(변경 안 하면 오류 발생)
        channelService.joinChannel(user1.getId(), channel2.getId());
        channelService.changeChannelOwner(user2.getId(), channel2.getId(), user1.getId());

        System.out.println("(전)전체 유저 리스트 = " + userService.findAllUsers());
        System.out.println("(전)전체 채널 리스트 = " + channelService.findAllChannels());
        System.out.println("(전)전체 메세지 리스트 = " + messageService.findAllMessages());
        System.out.println("===== [user2 삭제 시작] =====");
        // 삭제하려는 user2의 모든 메세지 삭제
        try {
            deleteUser(user2.getId(), userService, channelService, messageService);
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println(e);
        }
        System.out.println("===== [user2 삭제 종료] =====");
        System.out.println("(후)전체 유저 리스트 = " + userService.findAllUsers());
        System.out.println("(후)전체 채널 리스트 = " + channelService.findAllChannels());
        System.out.println("(후)전체 메세지 리스트 = " + messageService.findAllMessages());
        System.out.println("(후)[channel2의 message list] = " + messageService.findChannelMessagesByChannelId(channel2.getId()));
    }

    // 임시 테스트 메소드
    public static List<User> joinMembers(UserService userService, ChannelService channelService, UUID channelId) {
        return channelService.findMemberIdsByChannelId(channelId).stream()
                .map(id -> userService.findUserById(id))
                .flatMap(Optional::stream)
                .toList();
    }

    // 해당 유저가 owner인 channel 리스트
    public static List<Channel> ownerChannelList(UserService userService, ChannelService channelService, UUID userId) {
        return channelService.findOwnerChannelsByUserId(userId);
    }

    // 채널 삭제
    public static void deleteChannel(UUID userId, UUID channelId, ChannelService channelService, MessageService messageService) {
        // 삭제하려는 채널의 모든 메세지 삭제
        for (Message message : messageService.findChannelMessagesByChannelId(channelId)) {
            messageService.deleteMessage(userId, message.getId());
        }
        System.out.println("channel1 메세지 삭제 완료");

        // 해당 채널 삭제
        channelService.deleteChannel(userId, channelId);
        System.out.println("channel1 삭제 완료");
    }

    // 유저 삭제 메소드
    public static void deleteUser(UUID userId, UserService userService, ChannelService channelService, MessageService messageService) {
        // 삭제하려는 user1이 owner인 채널이 있는지 확인 후 없으면 채널 나가기
        if (!channelService.findOwnerChannelsByUserId(userId).isEmpty()) {
            throw new IllegalStateException("현재 owner인 channel이 존재합니다. 먼저 채널을 변경하세요.");
        }

        // 삭제하려는 user의 모든 메세지 삭제
        for (Message message : messageService.findUserMessagesByUserId(userId)) {
            messageService.deleteMessage(userId, message.getId());
        }
        System.out.println("해당 user 메세지 삭제 완료");

        for (Channel channel : channelService.findJoinChannelIdsByUserId(userId)) {
            channelService.leaveChannel(userId, channel.getId());
        }
        System.out.println("성공: 해당 user가 참여한 채널 나가기");

        // user2 삭제
        userService.deleteUser(userId);
        System.out.println("해당 user 삭제 완료");
    }
}
