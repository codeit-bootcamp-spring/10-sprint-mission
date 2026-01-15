package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        final JCFUserService userService = new JCFUserService();
        final JCFChannelService channelService = new JCFChannelService();
        final JCFMessageService messageService = new JCFMessageService();

        // 사용자 테스트 (현재 user2, user3 유효)
        User user1 = null;
        User user2 = null;
        User user3 = null;
        User user4 = null;

        // 생성
        try {   // 정상
            user1 = userService.createUser("dlekthf0906@codeit.com", "1234567890", "LeeDyol", UserStatusType.ONLINE);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }

        try {   // 정상
            user2 = userService.createUser("dlekthf@codeit.com", "1234567890", "dyoool", UserStatusType.OFFLINE);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }

        try {   // 정상
            user3 = userService.createUser("Yushi@codeit.com", "1234567890", "tokuno", UserStatusType.DND);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }

        try {   // 이메일 중복
            user4 = userService.createUser("Yushi@codeit.com", "1234567890", "yushi", UserStatusType.AWAY);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }

        // 단건 조회
        if (user1 != null) {
            try {       // 예상 출력: LeeDyol
                System.out.println("단건 조회 테스트: " + userService.searchUser(user1.getId()).getNickname());
            } catch (Exception e) {
                System.err.println("[단건 조회 실패] " + e.getMessage());
            }
        }

        // 전체 조회
        ArrayList<User> users = userService.searchUserAll();

        try {   // 에상 출력: Lee Dyol, dyoool, tokuno
            System.out.println("전체 조회 테스트");
            users.forEach(user -> System.out.println(user.getNickname()));
        } catch (Exception e) {
            System.err.println("[전체 조회 실패] " + e.getMessage());
        }

        // 수정
        if (user3 != null) {
            try {   // 비밀번호 변경 실패
                User updateUser = userService.updateUser(user3.getId(), "", "sakuya", null);
                System.out.println("수정 테스트: " + updateUser.getNickname());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null) {
            try {   // 닉네임 변경 실패
                User updateUser = userService.updateUser(user3.getId(), "1234", "", null);
                System.out.println("수정 테스트: " + updateUser.getNickname());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null) {
            try {   // 이전과 같은 비밀번호
                User updateUser = userService.updateUser(user3.getId(), "1234", "sakuya", null);
                System.out.println("수정 테스트: " + updateUser.getNickname());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null) {
            try {   // 예상 출력: sakuya
                User updateUser = userService.updateUser(user3.getId(), null, "sakuya", null);
                System.out.println("수정 테스트: " + updateUser.getNickname());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (user1 != null) {
            try {   // 예상 출력: dyoool, sakuya
                userService.deleteUser(user1.getId());
                System.out.println("삭제 테스트");
                users.forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e) {
                System.err.println("[삭제 실패] " + e.getMessage());
            }
        }

        try {   // 존재하지 않는 사용자
            userService.deleteUser(UUID.randomUUID());
            System.out.println("삭제 테스트");
            users.forEach(user -> System.out.println(user.getNickname()));
        } catch (Exception e) {
            System.err.println("[삭제 실패] " + e.getMessage());
        }

        System.out.println("=========================");

        // 채널 테스트 (현재 channel2만 유효)
        Channel channel1 = null;
        Channel channel2 = null;
        Channel channel3 = null;

        // 생성 테스트
        if (user3 != null) {
            try {
                channel1 = channelService.createChannel("Codeit", user3.getId(), ChannelType.CHAT);
            } catch (Exception e) {
                System.err.println("[생성 실패] " + e.getMessage());
            }
        }

        if (user3 != null) {
            try {
                channel2 = channelService.createChannel("Book Club", user3.getId(), ChannelType.VOICE);
            } catch (Exception e) {
                System.err.println("[생성 실패] " + e.getMessage());
            }
        }

        // if (user1 != null) {
        try {        // 존재하지 않는 사용자
            channel3 = channelService.createChannel("Running Club", user1.getId(), ChannelType.CHAT);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }
        // }

        // 단건 조회
        if (channel1 != null) {
            try {   // 예상 출력: Codeit
                System.out.println("단건 조회 테스트: " + channelService.searchChannel(channel1.getId()).getChannelName());
            } catch (Exception e) {
                System.err.println("[단건 조회 실패] " + e.getMessage());
            }
        }

        // 전체 조회
        ArrayList<Channel> channels = channelService.searchChannelAll();

        try {   // 에상 출력: Codeit, Book Club
            System.out.println("전체 조회 테스트");
            channels.forEach(channel -> System.out.println(channel.getChannelName()));
        } catch (Exception e) {
            System.err.println("[전체 조회 실패] " + e.getMessage());
        }

        // 수정
        if (channel2 != null) {
            try {   // 채널명 변경 실패
                Channel updateChannel = channelService.updateChannel(channel2.getId(), " ");
                System.out.println("수정 테스트: " + updateChannel.getChannelName());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (channel2 != null) {
            try {   // 예상 출력: Study Club
                Channel updateChannel = channelService.updateChannel(channel2.getId(), "Study Club");
                System.out.println("수정 테스트: " + updateChannel.getChannelName());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (channel1 != null) {
            try {   // 예상 출력: Study Club
                channelService.deleteChannel(channel1.getId());
                System.out.println("삭제 테스트");
                channels.forEach(channel -> System.out.println(channel.getChannelName()));
            } catch (Exception e) {
                System.err.println("[삭제 실패] " + e.getMessage());
            }
        }

        try {   // 존재하지 않는 채널
            channelService.deleteChannel(UUID.randomUUID());
            System.out.println("삭제 테스트");
            channels.forEach(channel -> System.out.println(channel.getChannelName()));
        } catch (Exception e) {
            System.err.println("[삭제 실패] " + e.getMessage());
        }

        // 초대
        if (channel2 != null && user2 != null) {
            try {       // 예상 출력: sakuya, dyoool
                channelService.inviteMembers(user2.getId(), channel2.getId(), channel2.getMembers());
                System.out.println("초대 테스트");
                channel2.getMembers().forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e) {
                System.err.println("[초대 실패] " + e.getMessage());
            }
        }

        if (channel2 != null && user3 != null) {
            try {       // 이미 존재하는 사용자
                channelService.inviteMembers(user3.getId(), channel2.getId(), channel2.getMembers());
                System.out.println("초대 테스트");
                channel2.getMembers().forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e) {
                System.err.println("[초대 실패] " + e.getMessage());
            }
        }

        // 퇴장
        try {       // 예상 출력: sakuya
            channelService.leaveMembers(user2.getId(), channel2.getId(), channel2.getMembers());
            System.out.println("퇴장 테스트");
            channel2.getMembers().forEach(user -> System.out.println(user.getNickname()));
        }  catch (Exception e) {
            System.err.println("[퇴장 실패] " + e.getMessage());
        }

        try {       // 채널에 존재하지 않는 사용자
            channelService.leaveMembers(user2.getId(), channel2.getId(), channel2.getMembers());
            System.out.println("퇴장 테스트");
            channel2.getMembers().forEach(user -> System.out.println(user.getNickname()));
        }  catch (Exception e) {
            System.err.println("[퇴장 실패] " + e.getMessage());
        }

        // 사용자 별 채널 리스트 조회
        try {
            System.out.println("특정 사용자가 참가한 채널 리스트 조회 테스트");
            List<Channel> userChannels = channelService.searchChannelsByUserId(user3.getId());
            userChannels.forEach(channel -> System.out.println(channel.getChannelName()));
        } catch (Exception e) {
            System.err.println("[사용자의 채널 목록 조회 실패] " + e.getMessage());
        }

        try {   // 해당 사용자가 존재하지 않음
            System.out.println("특정 사용자가 참가한 채널 리스트 조회 테스트");
            List<Channel> userChannels = channelService.searchChannelsByUserId(user1.getId());
            userChannels.forEach(channel -> System.out.println(channel.getChannelName()));
        } catch (Exception e) {
            System.err.println("[사용자의 채널 목록 조회 실패] " + e.getMessage());
        }

        // 특정 채널의 참가자 리스트 조회
        try {
            System.out.println("특정 채널의 참가자 리스트 조회 테스트");
            List<User> channelUsers = userService.searchUsersByChannelId(channel2.getId());
            channelUsers.forEach(channel -> System.out.println(channel.getNickname()));
        } catch (Exception e) {
            System.err.println("[채널의 참가자 목록 조회 실패] " + e.getMessage());
        }

        try {   // 해당 채널이 존재하지 않음
            System.out.println("특정 채널의 참가자 리스트 조회 테스트");
            List<User> channelUsers = userService.searchUsersByChannelId(channel1.getId());
            channelUsers.forEach(channel -> System.out.println(channel.getNickname()));
        } catch (Exception e) {
            System.err.println("[채널의 참가자 목록 조회 실패] " + e.getMessage());
        }

        System.out.println("=========================");

        // 메시지 테스트 (현재 message2만 유효)
        Message message1 = null;
        Message message2 = null;
        Message message3 = null;
        Message message4 = null;

        // 생성
        if (user3 != null && channel2 != null) {
            try {
                message1 = messageService.createMessage("안녕하세요", user3.getId(), channel2.getId(), MessageType.CHAT);
            } catch (Exception e) {
                System.err.println("[생성 실패] " + e.getMessage());
            }
        }

        if (user3 != null && channel2 != null) {
            try {
                message2 = messageService.createMessage("안녕 못하네요.", user3.getId(), channel2.getId(), MessageType.CHAT);
            } catch (Exception e) {
                System.err.println("[생성 실패] " + e.getMessage());
            }
        }

        // if (user3 != null && channel2 != null) {
        try {        // 존재하지 않는 사용자
            message3 = messageService.createMessage("저는 안녕해요", user1.getId(), channel2.getId(), MessageType.CHAT);
        } catch (Exception e) {
            System.err.println("[생성 실패] " + e.getMessage());
        }
        //}

        if (user2 != null && channel2 != null) {
            try {
                message4 = messageService.createMessage("꾸루루삥뽕", user2.getId(), channel2.getId(), MessageType.CHAT);
            } catch (Exception e) {
                System.err.println("[생성 실패] " + e.getMessage());
            }
        }

        // 단건 조회
        if (message1 != null) {
            try {   // 예상 출력: 안녕하세요
                System.out.println("단건 조회 테스트: " + messageService.searchMessage(message1.getId()).getMessage());
            } catch (Exception e) {
                System.err.println("[단건 조회 실패] " + e.getMessage());
            }
        }

        // 전체 조회
        ArrayList<Message> messages = messageService.searchMessageAll();

        try {   // 예상 출력: 안녕하세요, 안녕 못하네요
            System.out.println("전체 조회 테스트");
            messages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[전체 조회 실패] " + e.getMessage());
        }

        // 수정
        if (message2 != null) {
            try {   // 메시지 변경 실패
                Message updateMessage = messageService.updateMessage(message2.getId(), "");
                System.out.println("수정 테스트: " + updateMessage.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (message2 != null) {
            try {   // 예상 출력: 죄송해요 안녕합니다
                Message updateMessage = messageService.updateMessage(message2.getId(), "죄송해요. 안녕합니다");
                System.out.println("수정 테스트: " + updateMessage.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (message1 != null) {
            try {   // 예상 출력: 죄송해요 안녕합니다
                messageService.deleteMessage(message1.getId());
                System.out.println("삭제 테스트");
                messages.forEach(message -> System.out.println(message.getMessage()));
            } catch (Exception e) {
                System.err.println("[삭제 실패] " + e.getMessage());
            }
        }

        try {   // 존재하지 않는 메시지
            messageService.deleteMessage(UUID.randomUUID());
            System.out.println("삭제 테스트");
            messages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[삭제 실패] " + e.getMessage());
        }

        // 특정 사용자가 전송한 메시지 모록 조회
        try {
            System.out.println("특정 사용자가 전송한 메시지 목록 조회 테스트");
            List<Message> userMessages = messageService.searchMessagesByUserId(user3.getId());
            userMessages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[사용자 메시지 목록 조회 실패] " + e.getMessage());
        }

        try {       // 존재하지 않는 사용자
            System.out.println("특정 사용자가 전송한 메시지 목록 조회 테스트");
            List<Message> userMessages = messageService.searchMessagesByUserId(user1.getId());
            userMessages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[사용자 메시지 목록 조회 실패] " + e.getMessage());
        }

        // 특정 채널에서 발송된 메시지 목록 조회 테스트
        try {
            System.out.println("특정 채널에서 발송된 메시지 목록 조회 테스트");
            List<Message> channelMessages = messageService.searchMessagesByChannelId(channel2.getId());
            channelMessages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[채널 메시지 목록 조회 실패] " + e.getMessage());
        }

        try {       // 존재하지 않는 채널
            System.out.println("특정 채널에서 발송된 메시지 목록 조회 테스트");
            List<Message> channelMessages = messageService.searchMessagesByChannelId(channel1.getId());
            channelMessages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[사용자 메시지 목록 조회 실패] " + e.getMessage());
        }

        // 유저 삭제 -> 전체 채널 목록 및 전체 메시지 목록 연쇄 삭제
        if (channel2 != null && user2 != null) {
            try {       // 예상 출력: sakuya, dyoool
                channelService.inviteMembers(user2.getId(), channel2.getId(), channel2.getMembers());
                System.out.println("특정 채널 초대 테스트");
                channel2.getMembers().forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e) {
                System.err.println("[특정 채널 초대 실패] " + e.getMessage());
            }
        }

        if (user3 != null) {
            try {   // 예상 출력: dyoool
                userService.deleteUser(user3.getId());
                System.out.println("특정 채널 내 사용자 삭제 테스트");
                users.forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e) {
                System.err.println("[특정 체널 내 사용자 삭제 실패] " + e.getMessage());
            }
        }

        try {   // 에상 출력: Codeit, Book Club
            System.out.println("해당 채널의 참가자 전체 조회 테스트");
            channels.stream()
                    .flatMap(channel -> channel.getMembers().stream())
                    .forEach(user -> System.out.println(user.getNickname()));
        } catch (Exception e) {
            System.err.println("[전체 조회 실패] " + e.getMessage());
        }

        try {   // 예상 출력: 꾸루루삥뽕
            System.out.println("해당 채널에서 발송된 메시지 전체 조회 테스트");
            messages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e) {
            System.err.println("[전체 조회 실패] " + e.getMessage());
        }
    }
}