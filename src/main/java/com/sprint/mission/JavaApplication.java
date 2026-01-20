package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // Service 생성 !!
        Factory factory = new Factory("JCF");
        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();

        // test용 randomUUID
        UUID randomId = UUID.randomUUID();

        System.out.println("========================================");
        System.out.println("===== TEST START =====");
        System.out.println("========================================\n");

        // ============================================================
        // 1. User 생성
        // ============================================================
        System.out.println("=== [1] User CREATE ===");
        User user1 = userService.create("JEON");
        User user2 = userService.create("KIM");
        User user3 = userService.create("PARK");
        User user4 = userService.create("Lee");
        System.out.println("Created users: user1=" + user1.getId() + ", user2=" + user2.getId()
                + ", user3=" + user3.getId() + ", user4=" + user4.getId() + "\n");

        // ============================================================
        // 2. Channel 생성
        // ============================================================
        System.out.println("=== [2] Channel CREATE ===");
        Channel ch1 = channelService.create("codeit");
        Channel ch2 = channelService.create("codeTree");
        Channel ch3 = channelService.create("Baekjoon");
        Channel ch4 = channelService.create("LOL");
        System.out.println("Created channels: ch1=" + ch1.getId() + ", ch2=" + ch2.getId()
                + ", ch3=" + ch3.getId() + ", ch4=" + ch4.getId() + "\n");

        // ============================================================
        // 3. Channel JOIN/LEAVE
        // ============================================================
        System.out.println("=== [3] Channel JOIN/LEAVE ===");
        channelService.joinChannel(user1.getId(), ch1.getId());
        channelService.joinChannel(user2.getId(), ch1.getId());
        channelService.joinChannel(user3.getId(), ch1.getId());
        channelService.joinChannel(user4.getId(), ch1.getId());
        channelService.joinChannel(user1.getId(), ch2.getId());
        channelService.joinChannel(user2.getId(), ch2.getId());
        channelService.joinChannel(user4.getId(), ch3.getId());
        channelService.joinChannel(user4.getId(), ch4.getId());

        // user4, ch1 -> 파일에서 불러와야 함
        user4 = userService.find(user4.getId());
        ch1 = channelService.find(ch1.getId());

        System.out.println("user4.channels (before leave): " + user4.getChannels());
        System.out.println("ch1.members (before leave): " + ch1.getMembersList());

        channelService.leaveChannel(user4.getId(), ch1.getId());

        // user4, ch1 -> 파일에서 불러와야 함
        user4 = userService.find(user4.getId());
        ch1 = channelService.find(ch1.getId());

        System.out.println("user4.channels (after leave): " + user4.getChannels());
        System.out.println("ch1.members (after leave): " + ch1.getMembersList() + "\n");

        // ============================================================
        // 4. Message 생성
        // ============================================================
        System.out.println("=== [4] Message CREATE ===");
        Message msg1 = messageService.create("Hello everyone", user1.getId(), ch1.getId());
        Message msg2 = messageService.create("Have a nice Day", user2.getId(), ch1.getId());
        Message msg3 = messageService.create("GOOD", user3.getId(), ch1.getId());
        Message u1_ch1_extra = messageService.create("u1 extra in ch1", user1.getId(), ch1.getId());
        Message u3_ch1_extra = messageService.create("u3 extra in ch1", user3.getId(), ch1.getId());
        Message u1_ch2_m1 = messageService.create("u1 in ch2 #1", user1.getId(), ch2.getId());
        Message u2_ch2_m1 = messageService.create("u2 in ch2 #1", user2.getId(), ch2.getId());
        Message u1_ch2_m2 = messageService.create("u1 in ch2 #2", user1.getId(), ch2.getId());
        System.out.println("Created messages: msg1=" + msg1.getId() + ", msg2=" + msg2.getId()
                + ", msg3=" + msg3.getId() + "\n");

        // ============================================================
        // 5. 예외 처리 테스트 - User
        // ============================================================
        System.out.println("=== [5] Exception Test - User ===");
        try { userService.find(randomId); }
        catch (IllegalArgumentException e) { System.out.println("User find: " + e.getMessage()); }

        try { userService.updateName(randomId, "HAHHA"); }
        catch (IllegalArgumentException e) { System.out.println("User updateName: " + e.getMessage()); }

        try { userService.deleteUser(randomId); }
        catch (IllegalArgumentException e) { System.out.println("User delete: " + e.getMessage()); }
        System.out.println();

        // ============================================================
        // 6. 예외 처리 테스트 - Channel
        // ============================================================
        System.out.println("=== [6] Exception Test - Channel ===");
        try { channelService.find(randomId); }
        catch (IllegalArgumentException e) { System.out.println("Channel find: " + e.getMessage()); }

        try { channelService.updateName(randomId, "OMG"); }
        catch (IllegalArgumentException e) { System.out.println("Channel updateName: " + e.getMessage()); }

        try { channelService.deleteChannel(randomId); }
        catch (IllegalArgumentException e) { System.out.println("Channel delete: " + e.getMessage()); }
        System.out.println();

        // ============================================================
        // 7. 예외 처리 테스트 - Message
        // ============================================================
        System.out.println("=== [7] Exception Test - Message ===");
        try { messageService.find(randomId); }
        catch (IllegalArgumentException e) { System.out.println("Message find: " + e.getMessage()); }

        try { messageService.updateName(randomId, "HAHHA"); }
        catch (IllegalArgumentException e) { System.out.println("Message update: " + e.getMessage()); }

        try { messageService.deleteMessage(randomId); }
        catch (IllegalArgumentException e) { System.out.println("Message delete: " + e.getMessage()); }
        System.out.println();

        // ============================================================
        // 8. 예외 처리 테스트 - Channel Member
        // ============================================================
        System.out.println("=== [8] Exception Test - Channel Member ===");
        try { channelService.joinChannel(user1.getId(), randomId); }
        catch (IllegalArgumentException e) { System.out.println("joinChannel: " + e.getMessage()); }

        try { channelService.leaveChannel(user1.getId(), randomId); }
        catch (IllegalArgumentException e) { System.out.println("leaveChannel: " + e.getMessage()); }
        System.out.println();

        // ============================================================
        // 9. FIND / FINDALL 테스트
        // ============================================================
        System.out.println("=== [9] FIND / FINDALL ===");
        System.out.println("User find(user1): " + userService.find(user1.getId()));
        System.out.println("User findAll: " + userService.findAll());
        System.out.println("Channel find(ch1): " + channelService.find(ch1.getId()));
        System.out.println("Channel findAll: " + channelService.findAll());
        System.out.println("Message find(msg2): " + messageService.find(msg2.getId()));
        System.out.println("Message findAll: " + messageService.findAll() + "\n");

        // ============================================================
        // 10. UPDATE 테스트
        // ============================================================
        System.out.println("=== [10] UPDATE ===");
        userService.updateName(user2.getId(), "DAVID");
        System.out.println("User2 updated name: " + userService.find(user2.getId()).getName());

        channelService.updateName(ch1.getId(), "CODEIT_10");
        System.out.println("Ch1 updated name: " + channelService.find(ch1.getId()).getName());

        messageService.updateName(msg2.getId(), "My name is DAVID");
        System.out.println("Msg2 updated contents: " + messageService.find(msg2.getId()).getContents() + "\n");

        // ============================================================
        // 11. DELETE 테스트 - User 삭제
        // ============================================================

        user3 = userService.find(user3.getId());
        ch1 = channelService.find(ch1.getId());

        System.out.println("=== [11] DELETE Test - User ===");
        System.out.println("Before deleteUser(user3):");
        System.out.println("  Total messages: " + messageService.findAll().size());
        System.out.println("  Ch1 messages: " + ch1.getMessageList());
        System.out.println("  User3 messages: " + user3.getMessageList());

        userService.deleteUser(user3.getId());

        System.out.println("After deleteUser(user3):");
        ch1 = channelService.find(ch1.getId());
        System.out.println("  Total messages: " + messageService.findAll().size());
        System.out.println("  Ch1 messages: " + ch1.getMessageList());

        boolean u3InCh1 = ch1.getMessageList().stream()
                .anyMatch(m -> m.getContents().contains("u3") || m.getSender().getName().equals("PARK"));
        boolean u3InGlobal = messageService.findAll().stream()
                .anyMatch(m -> m.getSender().getName().equals("PARK"));
        System.out.println("  User3 messages still in ch1? " + u3InCh1);
        System.out.println("  User3 messages still in global? " + u3InGlobal + "\n");

        // ============================================================
        // 12. DELETE 테스트 - Channel 삭제
        // ============================================================

        user1 = userService.find(user1.getId());
        user2 = userService.find(user2.getId());
        ch2 = channelService.find(ch2.getId());

        System.out.println("=== [12] DELETE Test - Channel ===");
        System.out.println("Before deleteChannel(ch2):");
        System.out.println("  Total channels: " + channelService.findAll().size());
        System.out.println("  Total messages: " + messageService.findAll().size());
        System.out.println("  Ch2 messages: " + ch2.getMessageList());
        System.out.println("  User1 messages: " + user1.getMessageList());
        System.out.println("  User2 messages: " + user2.getMessageList());

        var ch2MessageIds = ch2.getMessageList().stream().map(Message::getId).toList();

        channelService.deleteChannel(ch2.getId());


        System.out.println("After deleteChannel(ch2):");
        System.out.println("  Total channels: " + channelService.findAll().size());
        System.out.println("  Total messages: " + messageService.findAll().size());
        // Message가 비지 않음 ... -> ch2가 삭제되서 객체 최신화를 못함 .. 여기 체크
        System.out.println("  Ch2 messageList empty? " + ch2.getMessageList().isEmpty());

        user1 = userService.find(user1.getId());
        user2 = userService.find(user2.getId());

        boolean ch2InGlobal = messageService.findAll().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));
        boolean ch2InUser1 = user1.getMessageList().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));
        boolean ch2InUser2 = user2.getMessageList().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));

        System.out.println("  Ch2 messages still in global? " + ch2InGlobal);
        System.out.println("  Ch2 messages still in user1? " + ch2InUser1);
        System.out.println("  Ch2 messages still in user2? " + ch2InUser2 + "\n");

        // JavaApplication.java 13번 직전
        System.out.println("Now ID: " + messageService.findAll().stream().map(Message::getId).toList());

        // ============================================================
        // 13. DELETE 테스트 - Message 삭제
        // ============================================================
        System.out.println("=== [13] DELETE Test - Message ===");
        msg1 = messageService.find(msg1.getId());
        UUID msg1ID = msg1.getId();
        UUID msg1SenderId = msg1.getSender().getId();
        UUID msg1ChannelId = msg1.getChannel().getId();

        User msg1Sender = userService.find(msg1SenderId);
        Channel msg1Channel = channelService.find(msg1ChannelId);


        System.out.println("Before deleteMessage(msg1):");
        System.out.println("  Total messages: " + messageService.findAll());
        System.out.println("  Sender messages: " + msg1Sender.getMessageList());
        System.out.println("  Channel messages: " + msg1Channel.getMessageList());

        messageService.deleteMessage(msg1.getId());

        msg1Sender = userService.find(msg1SenderId);
        msg1Channel = channelService.find(msg1ChannelId);

        System.out.println("After deleteMessage(msg1):");
        System.out.println("  Total messages: " + messageService.findAll());
        System.out.println("  Sender messages: " + msg1Sender.getMessageList());
        System.out.println("  Channel messages: " + msg1Channel.getMessageList() + "\n");

        // ============================================================
        // 14. 추가 기능 테스트
        // ============================================================
        System.out.println("=== [14] Additional Features ===");
        System.out.println("Ch1 members: " + channelService.findMembers(ch1.getId()));
        System.out.println("Ch1 messages: " + messageService.findMessagesByChannel(ch1.getId()));
        System.out.println("User1 messages: " + messageService.findMessagesByUser(user1.getId()));
        System.out.println("User2 messages: " + messageService.findMessagesByUser(user2.getId()));
        System.out.println("User4 joined channels: " + userService.findJoinedChannels(user4.getId()) + "\n");

        // ============================================================
        // 15. 직렬화 확인 - Channel , file, basic-file 일 경우에만 실행되도록
        // ============================================================
        if (factory.getMode().equals("file") || factory.getMode().equals("basic-file")) {
            System.out.println("=== [15] Serialization Check - Channel ===");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/channel/channel.ser"))) {
                List<Channel> channels = (List<Channel>) ois.readObject();
                System.out.println("Channels loaded: " + channels.size());
                channels.forEach(ch -> System.out.println("  id=" + ch.getId() + ", name=" + ch.getName()
                        + ", members=" + ch.getMembersList().size() + ", messages=" + ch.getMessageList().size()));
            } catch (Exception e) {
                System.out.println("Channel serialization failed: " + e.getMessage());
            }
            System.out.println();

            // ============================================================
            // 16. 직렬화 확인 - User
            // ============================================================
            System.out.println("=== [16] Serialization Check - User ===");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/user/user.ser"))) {
                List<User> users = (List<User>) ois.readObject();
                System.out.println("Users loaded: " + users.size());
                users.forEach(u -> System.out.println("  id=" + u.getId() + ", name=" + u.getName()
                        + ", channels=" + u.getChannels().size() + ", messages=" + u.getMessageList().size()));
            } catch (Exception e) {
                System.out.println("User serialization failed: " + e.getMessage());
            }
            System.out.println();

            // ============================================================
            // 17. 직렬화 확인 - Message
            // ============================================================
            System.out.println("=== [17] Serialization Check - Message ===");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/message/message.ser"))) {
                Map<UUID, Message> messages = (Map<UUID, Message>) ois.readObject();
                System.out.println("Messages loaded: " + messages.size());
                messages.values().forEach(m -> System.out.println("  id=" + m.getId() + ", sender=" + m.getSender().getName()
                        + ", channel=" + m.getChannel().getName() + ", content=" + m.getContents()));
            } catch (Exception e) {
                System.out.println("Message serialization failed: " + e.getMessage());
            }

            System.out.println("\n========================================");
            System.out.println("===== TEST END =====");
            System.out.println("========================================");

        }

    }
}