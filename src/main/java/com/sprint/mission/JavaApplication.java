package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // Service 생성
        Factory factory = new Factory();

        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();
        // 예외 케이스 테스트용 랜덤 id (존재하지 않는 UUID)
        UUID id = UUID.randomUUID();

        System.out.println("========");

        System.out.println("===Start Test===");

        System.out.println("\n=== [1] UserService: CREATE ===");
        // 등록 테스트
        User user1 = userService.create("JEON");
        User user2 = userService.create("KIM");
        User user3 = userService.create("PARK");
        User user4 = userService.create("Lee");

        System.out.println("Created users:");
        System.out.println("user1=" + user1.getId() + ", user2=" + user2.getId()
                + ", user3=" + user3.getId() + ", user4=" + user4.getId());

        // ============================================================
        // 2) ChannelService - CREATE
        // ============================================================
        System.out.println("\n===  ChannelService: CREATE ===");

        // 채널 생성
        Channel ch1 = channelService.create("codeit");
        Channel ch2 = channelService.create("codeTree");
        Channel ch3 = channelService.create("Baekjoon");
        Channel ch4 = channelService.create("LOL");

        System.out.println("Created channels:");
        System.out.println("ch1=" + ch1.getId() + ", ch2=" + ch2.getId()
                + ", ch3=" + ch3.getId() + ", ch4=" + ch4.getId());

        // ============================================================
        // 3) ChannelService - JOIN / LEAVE (정상 케이스)
        // ============================================================
        System.out.println("\n=== [3] ChannelService: JOIN / LEAVE (normal) ===");

        // ch1: user1~4 가입
        channelService.joinChannel(user1, ch1.getId());
        channelService.joinChannel(user2, ch1.getId());
        channelService.joinChannel(user3, ch1.getId());
        channelService.joinChannel(user4, ch1.getId());

        // 기타 채널 가입
        channelService.joinChannel(user1, ch2.getId());
        channelService.joinChannel(user2, ch2.getId());
        channelService.joinChannel(user4, ch3.getId());
        channelService.joinChannel(user4, ch4.getId());

        System.out.println("user4.channels(before leave) = " + user4.getChannels());
        System.out.println("ch1.members(before leave) = " + ch1.getMembersList());

        // user4가 ch1 탈퇴
        channelService.leaveChannel(user4, ch1.getId());
        System.out.println("user4.channels(after leave) = " + user4.getChannels());
        System.out.println("ch1.members(after leave) = " + ch1.getMembersList());

        // 메시지 생성
        Message msg1 = messageService.create("Hello everyone", user1.getId(), ch1.getId());
        Message msg2 = messageService.create("Have a nice Day", user2.getId(), ch1.getId());
        Message msg3 = messageService.create("GOOD", user3.getId(), ch1.getId());

        Message u1_ch1_extra = messageService.create("u1 extra in ch1", user1.getId(), ch1.getId());
        Message u3_ch1_extra = messageService.create("u3 extra in ch1", user3.getId(), ch1.getId());

        Message u1_ch2_m1 = messageService.create("u1 in ch2 #1", user1.getId(), ch2.getId());
        Message u2_ch2_m1 = messageService.create("u2 in ch2 #1", user2.getId(), ch2.getId());
        Message u1_ch2_m2 = messageService.create("u1 in ch2 #2", user1.getId(), ch2.getId());

        System.out.println("Created messages:");
        System.out.println("msg1=" + msg1.getId() + ", msg2=" + msg2.getId() + ", msg3=" + msg3.getId());

        // 예외 케이스 테스트
        System.out.println();
        System.out.println("===Exception Cases (Not Found)===");

        // User: find / updateName / delete
        try{
            System.out.println("User find" + userService.find(id));
        } catch (IllegalArgumentException e){
            System.out.println("User find EX " + e.getMessage());
        }

        try{
            userService.updateName(id, "HAHHA");
            System.out.println("User updateName Success");
        } catch (IllegalArgumentException e){
            System.out.println("User updateName EX " + e.getMessage());
        }

        try{
            userService.deleteUser(id);
            System.out.println("User Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("User delete EX " + e.getMessage());
        }

        // Channel: find / updateName / delete
        try{
            System.out.println("Channel find" + channelService.find(id));
            System.out.println("Channel find Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel find EX " + e.getMessage());
        }

        try{
            channelService.updateName(id, "OMG");
            System.out.println("Channel updateName Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel updateName EX " + e.getMessage());
        }

        try{
            channelService.deleteChannel(id);
            System.out.println("Channel Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel delete EX " + e.getMessage());
        }

        // Message: find / updateName / delete
        try{
            System.out.println("Message find" + messageService.find(id));
        } catch (IllegalArgumentException e){
            System.out.println("Message find EX " + e.getMessage());
        }

        try{
            messageService.update(id, "HAHHA");
            System.out.println("Message updateName Success");
        } catch (IllegalArgumentException e){
            System.out.println("Message updateName EX " + e.getMessage());
        }

        try{
            messageService.deleteMessage(id);
            System.out.println("Message Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("Message delete EX " + e.getMessage());
        }
        System.out.println("=======");

        // Member, Channel
        System.out.println();
        System.out.println("===Channel Member Exception Test===");

        try {
            channelService.joinChannel(user1, id); // 없는 channelId
        } catch (IllegalArgumentException e) {
            System.out.println("addMember EX - channel " + e.getMessage());
        }

        try {
            channelService.leaveChannel(user1, id); // 없는 channelId
        } catch (IllegalArgumentException e) {
            System.out.println("removeMember EX - channel " + e.getMessage());
        }
        System.out.println();

        // find & findAll test
        // User 조회
        System.out.println("\n=== FIND / FINDALL (normal) ===");

        System.out.println("[UserService] find(user1) = " + userService.find(user1.getId()));
        System.out.println("[UserService] findAll = " + userService.findAll());

        System.out.println("[ChannelService] find(ch1) = " + channelService.find(ch1.getId()));
        System.out.println("[ChannelService] findAll = " + channelService.findAll());

        System.out.println("[MessageService] find(msg2) = " + messageService.find(msg2.getId()));
        System.out.println("[MessageService] findAll = " + messageService.findAll());


        // updateName Test
        System.out.println("\n=== UPDATE (normal) ===");

        userService.updateName(user2.getId(), "DAVID");
        System.out.println("[UserService] user2.name = " + userService.find(user2.getId()).getName());

        channelService.updateName(ch1.getId(), "CODEIT_10");
        System.out.println("[ChannelService] ch1.name = " + channelService.find(ch1.getId()).getName());

        messageService.update(msg2.getId(), "My name is DAVID");
        System.out.println("[MessageService] msg2.contents = " + messageService.find(msg2.getId()).getContents());


        // ============================
        // Delete Test (삭제 반영 체크 포함)
        // ============================
        System.out.println("\n=== [9] DELETE + Relation Check ===");

        // ============================
        // Delete Test (삭제 반영 체크 포함) LLM 구현
        // ============================
        System.out.println("\n=== [9] DELETE + Relation Check (extended) ===");

        // ------------------------------------------------------------
        // A) User delete 검증: user3 삭제 시
        //  - messageService 전체에서 user3 메시지가 사라지는지
        //  - ch1.messageList에서 user3 메시지가 사라지는지
        //  - (참고) user3는 삭제되므로 user3.messageList는 이후 접근 의미 없음
        // ------------------------------------------------------------
        System.out.println("\n[A] deleteUser(user3) verification");

        System.out.println("Before deleteUser(user3):");
        System.out.println("messages.size=" + messageService.findAll().size());
        System.out.println("ch1.messages=" + ch1.getMessageList());
        System.out.println("user3.messages=" + user3.getMessageList());

        userService.deleteUser(user3.getId());

        System.out.println("After deleteUser(user3):");
        System.out.println("messages.size=" + messageService.findAll().size());
        System.out.println("ch1.messages=" + ch1.getMessageList());

        // user3가 보낸 메시지가 남아있는지 직접 체크(내용 기반)
        // - getContents가 있고, u3 메시지 내용에 "u3"를 넣어뒀으니 문자열로 검증
        boolean stillExistsU3InCh1 = ch1.getMessageList().stream()
                .anyMatch(m -> m.getContents().contains("u3") || m.getSender().getName().equals("PARK"));
        System.out.println("Check: user3 messages still in ch1? -> " + stillExistsU3InCh1);

        // messageService 전체에서도 user3 메시지 남았는지 체크
        boolean stillExistsU3Global = messageService.findAll().stream()
                .anyMatch(m -> m.getSender().getName().equals("PARK"));
        System.out.println("Check: user3 messages still in messageService? -> " + stillExistsU3Global);


        // ------------------------------------------------------------
        // B) Channel delete 검증: ch2 삭제 시
        // 1) ch2.messageList가 비었는지 (객체는 남아있으니 리스트로 체크 가능)
        // 2) messageService 전체에서 ch2 메시지가 사라지는지
        // 3) user1/user2 messageList에서 ch2 메시지가 사라지는지
        // ------------------------------------------------------------
        System.out.println("\n[B] deleteChannel(ch2) verification");

        // 삭제 전 스냅샷(검증 기준)
        System.out.println("Before deleteChannel(ch2):");
        System.out.println("channels.size=" + channelService.findAll().size());
        System.out.println("messages.size=" + messageService.findAll().size());
        System.out.println("ch2.messages=" + ch2.getMessageList());
        System.out.println("user1.messages=" + user1.getMessageList());
        System.out.println("user2.messages=" + user2.getMessageList());

        // ch2에 속한 메시지 ID들을 미리 확보 (삭제 후 글로벌에서 없어졌는지 확인용)
        var ch2MessageIds = ch2.getMessageList().stream().map(Message::getId).toList();

        channelService.deleteChannel(ch2.getId());

        System.out.println("After deleteChannel(ch2):");
        System.out.println("channels.size=" + channelService.findAll().size());
        System.out.println("messages.size=" + messageService.findAll().size());

        // (1) 채널 내부 messageList가 비었는지
        System.out.println("Check: ch2.messageList empty? -> " + ch2.getMessageList().isEmpty());

        // (2) messageService 전체에서 ch2 메시지들이 사라졌는지
        boolean anyCh2MessageLeftInGlobal = messageService.findAll().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));
        System.out.println("Check: ch2 messages still in messageService? -> " + anyCh2MessageLeftInGlobal);

        // (3) 유저의 messageList에서 ch2 메시지가 사라졌는지
        boolean anyCh2MessageLeftInUser1 = user1.getMessageList().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));
        boolean anyCh2MessageLeftInUser2 = user2.getMessageList().stream()
                .anyMatch(m -> ch2MessageIds.contains(m.getId()));

        System.out.println("Check: ch2 messages still in user1.messageList? -> " + anyCh2MessageLeftInUser1);
        System.out.println("Check: ch2 messages still in user2.messageList? -> " + anyCh2MessageLeftInUser2);


        // ------------------------------------------------------------
        // C) Message delete 검증: msg1 삭제 시
        //  - messageService에서 사라지는지
        //  - sender.messageList, channel.messageList에서 사라지는지
        // ------------------------------------------------------------
        System.out.println("\n[C] deleteMessage(msg1) verification");

        User msg1Sender = msg1.getSender();
        Channel msg1Channel = msg1.getChannel();

        System.out.println("Before deleteMessage(msg1):");
        System.out.println("messages=" + messageService.findAll());
        System.out.println("sender.messageList=" + msg1Sender.getMessageList());
        System.out.println("channel.messageList=" + msg1Channel.getMessageList());

        messageService.deleteMessage(msg1.getId());

        System.out.println("After deleteMessage(msg1):");
        System.out.println("messages=" + messageService.findAll());
        System.out.println("sender.messageList=" + msg1Sender.getMessageList());
        System.out.println("channel.messageList=" + msg1Channel.getMessageList());



//        // ---------- User delete: 유저 삭제 시 채널 탈퇴/메시지 삭제가 반영되는지 ----------
//        System.out.println("\n[UserService][deleteUser] user3");
//        System.out.println("Before deleteUser(user3): users=" + userService.findAll());
//        System.out.println("Before deleteUser(user3): ch1.members=" + channelService.findMembers(ch1.getId()));
//        System.out.println("Before deleteUser(user3): messages=" + messageService.findAll());
//
//        userService.deleteUser(user3.getId());
//
//        System.out.println("After deleteUser(user3): users=" + userService.findAll());
//        System.out.println("After deleteUser(user3): ch1.members=" + channelService.findMembers(ch1.getId()));
//        System.out.println("After deleteUser(user3): messages=" + messageService.findAll());
//
//        // ---------- Channel delete: 채널 삭제 시 유저 채널 목록/메시지 삭제 반영 ----------
//        System.out.println("\n[ChannelService][deleteChannel] ch2");
//        System.out.println("Before deleteChannel(ch2): channels=" + channelService.findAll());
//        System.out.println("Before deleteChannel(ch2): user1.channels=" + user1.getChannels());
//
//        channelService.deleteChannel(ch2.getId());
//
//        System.out.println("After deleteChannel(ch2): channels=" + channelService.findAll());
//        System.out.println("After deleteChannel(ch2): user1.channels=" + user1.getChannels());
//
//        // ---------- Message delete: 메시지 삭제 시 유저/채널 messageList 반영 ----------
//        System.out.println("\n[MessageService][deleteMessage] msg1");
//        User msg1Sender = msg1.getSender();
//        Channel msg1Channel = msg1.getChannel();
//
//        System.out.println("Before deleteMessage(msg1): messages=" + messageService.findAll());
//        System.out.println("Before deleteMessage(msg1): sender.messageList=" + msg1Sender.getMessageList());
//        System.out.println("Before deleteMessage(msg1): channel.messageList=" + msg1Channel.getMessageList());
//
//        messageService.deleteMessage(msg1.getId());
//
//        System.out.println("After deleteMessage(msg1): messages=" + messageService.findAll());
//        System.out.println("After deleteMessage(msg1): sender.messageList=" + msg1Sender.getMessageList());
//        System.out.println("After deleteMessage(msg1): channel.messageList=" + msg1Channel.getMessageList());


        // 추가 기능 테스트
        System.out.println();
        System.out.println("===Additional Feature Tests===");

        // 1) Channel members
        System.out.println("[1] Channel member names - ch1(" + ch1.getName() + ")");
        System.out.println(channelService.findMembers(ch1.getId()));

        // 2) Channel messages
        System.out.println();
        System.out.println("[2] Channel message contents - ch(" + ch1.getName() + ")");
        System.out.println(messageService.findMessagesByChannel(ch1.getId()));

        // 3) Messages sent by each user (contents)
        System.out.println();
        System.out.println("[3] Messages sent by users (message contents)");

        System.out.println("user1 (" + user1.getName() + "): " +
                messageService.findMessagesByUser(user1.getId()));

        System.out.println("user2 (" + user2.getName() + "): " +
                messageService.findMessagesByUser(user2.getId()));


        // 4) Channels joined by a user (channel names)
        System.out.println();
        System.out.println("[4] Joined channel names - user4 (" + user4.getName() + ")");
        System.out.println(userService.findJoinedChannels(user4.getId()));

        System.out.println("=== End of Additional Feature Tests ===");
        System.out.println();
    }
}