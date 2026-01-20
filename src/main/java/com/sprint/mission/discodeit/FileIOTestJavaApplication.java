package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileDataStore;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileIOTestJavaApplication {
    public static void main(String[] args) {
        System.out.println("========== 테스트 시작 ==========");
        /** 기본 세팅 (테스트 유저/채널/메세지 생성 시 아래와 같이 생성)
         * 테스트 유저(3명) : u1 , u2 , u3
         * 테스트 채널(3개) : c1 , c2 , c3
         * 테스트 메세지(3개) : m1 , m2 , m3
         * u1이 owner인 채널 : c1 , c3
         * u2가 owner인 채널 : c2
         * u1이 참여한 채널 : c1 , c3
         * u2가 참여한 채널 : c2
         * u1이 작성한 메세지 : m1 , m2
         * u2가 작성한 메세지 : m3
         * c1에 작성된 메세지 : m1 , m2 , m3
         */
        // 유저 생성 및 유저 정보 수정 테스트
        userCreateUpdateTest();

        loadUser(); // 유저 Load 후 유저 정보 확인

        // 채널 생성 및 채널 정보 수정 테스트
        channelCreateUpdateTest();

        // 유저의 채널 참가 후, 채널 owner 교체, 탈퇴 테스트
        channelJoinLeaveTest();

        loadChannel(); // 채널 Load 후 채널 정보 확인

        // 메세지 생성 및 메세지 정보 수정 테스트
        messageCreateUpdateTest();

        loadMessage(); // 메세지 Load 후 메세지 정보 확인

        // 메세지 삭제
        deleteMessageTest();
        loadMessage();

        // 채널 삭제
        m2Add(); // 앞에서 삭제한 m2 채우기
        deleteChannelTest(); // c1 삭제
        loadChannel();
        loadMessage();

        // 유저 삭제
        c1Add(); // 앞에서 삭제한 채널 채우기
        messageCreateUpdateTest(); // 앞에서 삭제한 메세지 채우기
        deleteUserTest(); // u1 삭제
        loadUser();
        loadChannel();
        loadMessage();

    }

    // User
    static void userCreateUpdateTest() {
        System.out.println("========== 유저 생성 및 유저 정보 수정 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        System.out.println("\n========== 유저 생성 u1, u2, u3 ==========");
        User u1 = userService.createUser("u1@email.com", "u1", "u1Nick", "1111", "20000101");
        User u2 = userService.createUser("u2@email.com", "u2", "u2Nick", "2222", "20000202");
        User u3 = userService.createUser("u3@email.com", "u3", "u3Nick", "3333", "20000303");
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 유저 정보 수정 테스트: u2 정보 수정 ==========");
        System.out.println("[전]u2 기본 정보 = " + userService.findUserById(u2.getId()).orElseThrow());
        userService.updateUserInfo(u2.getId(), null, null, "[update]u2", null, null);
        userService.updateUserInfo(u2.getId(), "u2u2@gmail.com", "3333", null, null, null);
//        try { // 하나씩 테스트해야 함.
//            // 변경 전과 동일한 값 입력
//            userService.updateUserInfo(u2.getId(), null, null, "[update]u2", null, null);
//            // 중복 이메일
//            userService.updateUserInfo(u2.getId(), "u1@email.com", null, "[update]u2", null, null);
//            // blank 존재
//            userService.updateUserInfo(u2.getId(), null, null, null, "", null);
//        } catch (IllegalArgumentException e) {
//            System.out.println(e);
//        }
        System.out.println("[후]u2 기본 정보 = " + userService.findUserById(u2.getId()).orElseThrow());

        System.out.println("\n========== 유저 정보 수정 원상 복귀 ==========");
        System.out.println("[전]u2 기본 정보 = " + userService.findUserById(u2.getId()).orElseThrow());
        try { // 동일 값, blank
            userService.updateUserInfo(u2.getId(), null, null, "[update]u2", null, null);
            userService.updateUserInfo(u2.getId(), "", "", null, null, null);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        userService.updateUserInfo(u2.getId(), "u2@email.com", "2222", "u2", null, null);
        System.out.println("[후]u2 기본 정보 = " + userService.findUserById(u2.getId()).orElseThrow());
    }

    static void loadUser() {
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());
    }

    // Channel
    static void channelCreateUpdateTest() {
        System.out.println("========== 채널 생성 및 채널 정보 수정 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();

        System.out.println("\n========== 채널 생성 c1, c2, c3 ==========");
        Channel c1 = channelService.createChannel(u1.getId(), ChannelType.PUBLIC, "c1u1PUBLIC", "c1Desu1PUBLIC");
        Channel c2 = channelService.createChannel(u2.getId(), ChannelType.PUBLIC, "c2u2PUBLIC", "c2Desu2PUBLIC");
        Channel c3 = channelService.createChannel(u1.getId(), ChannelType.PRIVATE, "c3u1PRIVATE", "c3Desu1PRIVATE");
        Channel c1Load = channelService.findChannelById(c1.getId()).orElseThrow();
        Channel c2Load = channelService.findChannelById(c2.getId()).orElseThrow();
        Channel c3Load = channelService.findChannelById(c3.getId()).orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());
        System.out.println("channel1의 channel memberList = " + c1Load.getChannelMembersList().stream().map(m -> m.getId()).toList() + " / channel2의 channel member list = " + c2Load.getChannelMembersList().stream().map(m -> m.getId()).toList() + " / channel3의 channel member list = " + c3Load.getChannelMembersList().stream().map(m -> m.getId()).toList());
        System.out.println("user1의 join channelList = " + u1.getJoinChannelList().stream().map(c -> c.getId()).toList());

        System.out.println("\n========== 채널 정보 수정 테스트: c2 수정 ==========");
        try { // 채널 owner가 아닐 경우
            channelService.updateChannelInfo(u1.getId(), c2.getId(), null, "[update]c2u1PUBLIC", null);
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        System.out.println("[전]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
        channelService.updateChannelInfo(u2.getId(), c2.getId(), ChannelType.PRIVATE, "[update]c2u2PRIVATE", null);
        System.out.println("[후]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
        System.out.println("[후]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        try {
            // 이전 값과 동일
            channelService.updateChannelInfo(u2.getId(), c2.getId(), null, "[update]c2u2PRIVATE", null);
            // blank
            channelService.updateChannelInfo(u2.getId(), c2.getId(), null, "", null);
        } catch(IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    static void channelJoinLeaveTest() {
        System.out.println("========== 유저의 채널 참가 후, 채널 owner 교체, 탈퇴 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();
        // user 기본 정보 로드
        User u1Load = userService.findUserById(u1.getId()).orElseThrow();
        User u2Load = userService.findUserById(u2.getId()).orElseThrow();
        User u3Load = userService.findUserById(u3.getId()).orElseThrow();
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        Channel c2 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("[update]c2u2PRIVATE")).findFirst().orElseThrow();
        Channel c3 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c3u1PRIVATE")).findFirst().orElseThrow();
        Channel c1Load = channelService.findChannelById(c1.getId()).orElseThrow();
        Channel c2Load = channelService.findChannelById(c2.getId()).orElseThrow();
        Channel c3Load = channelService.findChannelById(c3.getId()).orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

        System.out.println("\n========== u2가 c3에 참여 ==========");
        System.out.println("[전]user2의 join channelList = " + u2Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]channel3의 channel member list = " + c3Load.getChannelMembersList().stream().map(m -> m.getId()).toList());
        channelService.joinChannel(u2.getId(), c3.getId());
        System.out.println("[후]user2의 join channelList = " + u2Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]channel3의 channel member list = " + c3Load.getChannelMembersList().stream().map(m -> m.getId()).toList());
        try {
            System.out.print("user가 이미 해당 채널에 참여된 상태일 때: ");
            channelService.joinChannel(u2.getId(), c2.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        System.out.println("\n========== 채널 owner 수정 테스트: c3 owner(u1)을 u2로 변경 ==========");
        System.out.println("[전]channel1의 owner ID = " + c1Load.getOwner().getId() + " / channel2의 owner ID = " + c2Load.getOwner().getId() + " / channel3의 owner ID = " + c3Load.getOwner().getId());
        channelService.changeChannelOwner(u1.getId(), c3.getId(), u2.getId());
        System.out.println("[후]channel1의 owner ID = " + c1Load.getOwner().getId() + " / channel2의 owner ID = " + c2Load.getOwner().getId() + " / channel3의 owner ID = " + c3Load.getOwner().getId());
        try {
            System.out.print("owner로 바꾸려는 user가 미참여 채널일 때: ");
            channelService.changeChannelOwner(u1.getId(), c1.getId(), u2.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        System.out.println("\n========== u1이 c3에서 나가기 ==========");
        System.out.println("[전]user1의 join channelList = " + u1Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]user2의 join channelList = " + u2Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]channel3의 channel member list = " + c3Load.getChannelMembersList().stream().map(m -> m.getId()).toList());
        channelService.leaveChannel(u1.getId(), c3.getId());
        System.out.println("[후]user1의 join channelList = " + u1Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]user2의 join channelList = " + u2Load.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]channel3의 channel member list = " + c3Load.getChannelMembersList().stream().map(m -> m.getId()).toList());
    }

    static void loadChannel() {
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

//        System.out.println("u1의 참여 채널 리스트 = " + u1.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("\n========== 채널 정보 수정 테스트: c2 수정 ==========");
//        System.out.println("[전]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[전]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[전]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
//        channelService.updateChannelInfo(u2.getId(), c2.getId(), ChannelType.PUBLIC, "c2u2PUBLIC", null);
//        System.out.println("[후]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
//        System.out.println("[후]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[후]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
    }

    static void messageCreateUpdateTest() {
        System.out.println("========== 메세지 생성 및 메세지 정보 수정 테스트 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        Channel c2 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("[update]c2u2PRIVATE")).findFirst().orElseThrow();
        Channel c3 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c3u1PRIVATE")).findFirst().orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

        System.out.println("\n========== u2가 c1에 참여 ==========");
        try {
            channelService.joinChannel(u2.getId(), c1.getId());
            System.out.println("c1 참여 유저 = " + c1.getChannelMembersList().stream().map(m -> m.getId()).toList());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        System.out.println("\n========== 메세지 생성 m1, m2, m3 ==========");
        // message 생성
        Message m1 = messageService.createMessage(c1.getId(), u1.getId(), "m1c1u1");
        Message m2 = messageService.createMessage(c1.getId(), u1.getId(), "m2c1u1");
        Message m3 = messageService.createMessage(c1.getId(), u2.getId(), "m3c1u2");
        try {
            System.out.print("u3가 참여하지 않은 c2 채널에서 메세지를 작성: ");
            Message m4 = messageService.createMessage(c2.getId(), u3.getId(), "m4c2u2");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());

        System.out.println("\n========== 메세지 정보 수정 테스트: m2 수정 ==========");
        System.out.println("[전]m2의 content = " + messageService.findMessageById(m2.getId()).orElseThrow().getMessageContent());
        messageService.updateMessageContent(u1.getId(), m2.getId(), "[update]m2c1u1");
        System.out.println("[후]m2의 content = " + messageService.findMessageById(m2.getId()).orElseThrow().getMessageContent());
    }

    static void loadMessage() {
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        System.out.println("\n========== 메세지 Load m1, m2, m3 ==========");
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());
    }

    static void deleteMessageTest() {
        System.out.println("========== m2 메세지 삭제 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        Channel c2 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("[update]c2u2PRIVATE")).findFirst().orElseThrow();
        Channel c3 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c3u1PRIVATE")).findFirst().orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

        System.out.println("\n========== u2가 c1에 참여 ==========");
        System.out.println("c1 참여 유저 = " + channelService.findChannelById(c1.getId()).orElseThrow().getChannelMembersList().stream().map(m -> m.getId()).toList());

        System.out.println("\n========== 메세지 Load m1, m2, m3 ==========");
        Message m1 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m1c1u1")).findFirst().orElseThrow();
        Message m2 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("[update]m2c1u1")).findFirst().orElseThrow();
        Message m3 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m3c1u2")).findFirst().orElseThrow();
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());
        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());

        System.out.println("\n========== 메세지 m2 삭제 ==========");
        System.out.println("[전]u1의 Message List = " + messageService.findUserMessagesByUserId(u1.getId()).stream().map(m->m.getId()).toList());
        System.out.println("[전]c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());
        System.out.println("u1.writeMessageList = " + u1.getWriteMessageList().stream().map(m -> m.getId()).toList());
        System.out.println("c1.channelMessagesList = " + c1.getChannelMessagesList().stream().map(m -> m.getId()).toList());
        messageService.deleteMessage(u1.getId(), m2.getId());
        try {
            System.out.print("작성자도 owner도 아닌 제 3자가 삭제: ");
            messageService.deleteMessage(u3.getId(), m1.getId());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        // owner는 채널에 메세지 삭제 가능
//        messageService.deleteMessage(u1.getId(), m3.getId());
        System.out.println("[후]u1의 Message List = " + messageService.findUserMessagesByUserId(u1.getId()).stream().map(m->m.getId()).toList());
        System.out.println("[후]c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());
        System.out.println("u1.writeMessageList = " + u1.getWriteMessageList().stream().map(m -> m.getId()).toList());
        System.out.println("c1.channelMessagesList = " + c1.getChannelMessagesList().stream().map(m -> m.getId()).toList());
    }

    static void deleteChannelTest() {
        System.out.println("========== c1 채널 삭제 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        Channel c2 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("[update]c2u2PRIVATE")).findFirst().orElseThrow();
        Channel c3 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c3u1PRIVATE")).findFirst().orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

        System.out.println("\n========== u2가 c1에 참여 ==========");
        System.out.println("c1 참여 유저 = " + channelService.findChannelById(c1.getId()).orElseThrow().getChannelMembersList().stream().map(m -> m.getId()).toList());

        System.out.println("\n========== 메세지 Load m1, m2, m3 ==========");
        Message m1 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m1c1u1")).findFirst().orElseThrow();
        Message m2 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("[update]m2c1u1")).findFirst().orElseThrow();
        Message m3 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m3c1u2")).findFirst().orElseThrow();

        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());
        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());

        System.out.println("\n========== channel1 삭제 및 삭제 후 데이터 조회 ==========");
        System.out.println(channelService.findChannelById(c1.getId()));
        System.out.println("[전]channel1의 member list = " + c1.getChannelMembersList().stream().map(m -> m.getId()).toList());
        System.out.println("[전]user1의 join channel list = " + u1.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]user1의 message list = " + u1.getWriteMessageList().stream().map(m -> m.getId() + ": " + m.getMessageContent()).toList());
        System.out.println("[전]전체 message list = " + messageService.findAllMessages().stream().map(m -> m.getId()).toList());
        System.out.println("===== [채널 삭제 시작] =====");
        deleteChannel(u1.getId(), c1.getId(), channelService, messageService);
        System.out.println("===== [채널 삭제 종료] =====");
        System.out.println(channelService.findChannelById(c1.getId()));
//        System.out.println("[후]channel1의 member list = " + channelService.findChannelById(c1.getId()).orElseThrow().getChannelMembersList().stream().map(m -> m.getId()).toList());
        System.out.println("[후]channel1의 member list = " + c1.getChannelMembersList().stream().map(m -> m.getId()).toList());
//        System.out.println("[후]user1의 join channel list = " + userService.findUserById(u1.getId()).orElseThrow().getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]user1의 join channel list = " + u1.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]user1의 message list = " + u1.getWriteMessageList().stream().map(m -> m.getId() + ": " + m.getMessageContent()).toList());
        System.out.println("[후]전체 message list = " + messageService.findAllMessages().stream().map(m -> m.getId()).toList());
    }

    static void deleteUserTest() {
        System.out.println("========== u1 유저 삭제 테스트 시작 ==========");
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        User u3 = userService.findUserByEmailAndPassword("u3@email.com", "3333").orElseThrow();
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());

        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        Channel c2 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("[update]c2u2PRIVATE")).findFirst().orElseThrow();
        Channel c3 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c3u1PRIVATE")).findFirst().orElseThrow();
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

        System.out.println("\n========== u2가 c1에 참여 ==========");
        System.out.println("c1 참여 유저 = " + channelService.findChannelById(c1.getId()).orElseThrow().getChannelMembersList().stream().map(m -> m.getId()).toList());

        System.out.println("\n========== 메세지 Load m1, m2, m3 ==========");
        Message m1 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m1c1u1")).findFirst().orElseThrow();
        Message m2 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("[update]m2c1u1")).findFirst().orElseThrow();
        Message m3 = messageService.findAllMessages().stream().filter(m -> m.getMessageContent().equals("m3c1u2")).findFirst().orElseThrow();

        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());
        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());

        System.out.println("\n========== u2가 c1, c3에 참여 ==========");
//        channelService.joinChannel(u2.getId(), c1.getId()); // 이미 참여된 상태
//        channelService.joinChannel(u2.getId(), c3.getId()); // 이미 참여된 상태
        System.out.println("\n========== u2가 c1, c3의 owner로 변경 ==========");
//        channelService.changeChannelOwner(u1.getId(), c3.getId(), u2.getId()); // 이미 바뀐 상태
        channelService.changeChannelOwner(u1.getId(), c1.getId(), u2.getId());

        System.out.println("\n========== user1 삭제 ==========");
        System.out.println("[전]전체 user list = " + userService.findAllUsers().stream().map(u -> u.getUserName() + ": " + u.getId()).toList());
        System.out.println("[전]전체 channel List = " + channelService.findAllChannels().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[전]channel1의 member list = " + c1.getChannelMembersList().stream().map(m -> m.getUserName() + ": " + m.getId()).toList());
        System.out.println("[전]전체 message list = " + messageService.findAllMessages().stream().map(m -> m.getMessageContent() + ": " + m.getId()).toList());
        System.out.println("===== [유저 삭제 시작] =====");
        deleteUser(u1.getId(), userService, channelService, messageService);
        System.out.println("===== [유저 삭제 종료] =====");
        System.out.println("[후]전체 user list = " + userService.findAllUsers().stream().map(u -> u.getUserName() + ": " + u.getId()).toList());
        System.out.println("[후]전체 channel List = " + channelService.findAllChannels().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
        System.out.println("[후]channel1의 member list = " + c1.getChannelMembersList().stream().map(m -> m.getUserName() + ": " + m.getId()).toList());
        System.out.println("[후]전체 message list = " + messageService.findAllMessages().stream().map(m -> m.getMessageContent() + ": " + m.getId()).toList());
        System.out.println("u1 조회 = " + userService.findUserById(u1.getId()));
    }

    // 채널 삭제 메소드
    public static void deleteChannel(UUID ownerId, UUID channelId, ChannelService channelService, MessageService messageService) {
//        try {
//            System.out.print("owner가 아닌 유저가 채널 삭제 시도 시: ");
//            channelService.deleteChannel(user.getId(), channel.getId());
//        } catch (IllegalStateException | NoSuchElementException e) {
//            System.out.println(e);
//        }

        // 해당 user가 channel의 owner인지 확인을 위해 if문으로 예외 검사하기!!
        try {
            Channel channel = channelService.findChannelById(channelId).orElseThrow();
            if (!channel.getOwner().getId().equals(ownerId)) {
                throw new IllegalStateException("채널 삭제는 채널 owner만 수행 가능합니다.");
            }
            for (Message message : messageService.findChannelMessagesByChannelId(channelId)) {
                messageService.deleteMessage(ownerId, message.getId());
            }
            System.out.println("channel1의 메세지 삭제 완료");
            channelService.deleteChannel(ownerId, channelId);
            System.out.println("channel1 삭제 완료");
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println(e);
        }
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

        for (Channel channel : channelService.findJoinChannelsByUserId(userId)) {
            channelService.leaveChannel(userId, channel.getId());
        }
        System.out.println("성공: 해당 user가 참여한 채널 나가기");

        // user 삭제
        userService.deleteUser(userId);
        System.out.println("해당 user 삭제 완료");
    }

    public static void m2Add() {
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        Channel c1 = channelService.findAllChannels().stream().filter(c -> c.getChannelName().equals("c1u1PUBLIC")).findFirst().orElseThrow();
        // message 생성
        Message m2 = messageService.createMessage(c1.getId(), u1.getId(), "m2c1u1");
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());
        messageService.updateMessageContent(u1.getId(), m2.getId(), "[update]m2c1u1");
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());        System.out.println("c1의 Message List = " + messageService.findChannelMessagesByChannelId(c1.getId()).stream().map(m->m.getId()).toList());
    }
    public static void c1Add() {
        FileDataStore fileDataStore = FileDataStore.loadData();
        UserService userService = new FileUserService(fileDataStore);
        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        User u1 = userService.findUserByEmailAndPassword("u1@email.com", "1111").orElseThrow();
        User u2 = userService.findUserByEmailAndPassword("u2@email.com", "2222").orElseThrow();
        Channel c1 = channelService.createChannel(u1.getId(), ChannelType.PUBLIC, "c1u1PUBLIC", "c1Desu1PUBLIC");
        try {
            channelService.joinChannel(u2.getId(), c1.getId());
            System.out.println("c1 참여 유저 = " + c1.getChannelMembersList().stream().map(m -> m.getId()).toList());
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
    }
}
