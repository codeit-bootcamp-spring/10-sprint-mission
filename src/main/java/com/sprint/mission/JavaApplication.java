package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.NoSuchElementException;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        // User
        // 등록
        JCFUserService jcfU1 = new JCFUserService();
        UUID userId1 = jcfU1.create("홍길동").getId();
        JCFUserService jcfU2 = new JCFUserService();

        // 조회(단건)
        try {
            System.out.println(jcfU1.read(userId1));
        } catch (NoSuchElementException e) {
            System.out.println("해당 유저가 존재하지 않습니다.");
        }
        try {
            System.out.println(jcfU2.read(UUID.randomUUID()));
        } catch (NoSuchElementException e) {
            System.out.println("해당 유저가 존재하지 않습니다.");
        }

        // 조회(다건)
        System.out.println(jcfU1.readAll());
        System.out.println(jcfU2.readAll());

        // 수정
        try {
            jcfU1.update(userId1, "일이삼");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 유저가 존재하지 않습니다.");
        }
        try {
            jcfU1.update(UUID.randomUUID(), "아무거나 유저명");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 유저가 존재하지 않습니다.");
        }

        // 수정된 데이터 조회
        try {
            System.out.println(jcfU1.read(userId1));
        } catch (NoSuchElementException e) {
            System.out.println("조회할 유저가 존재하지 않습니다.");
        }

        // 삭제
        try {
            jcfU1.delete(userId1);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 유저가 존재하지 않습니다.");
        }
        try {
            jcfU1.delete(UUID.randomUUID());
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 유저가 존재하지 않습니다.");
        }

        // 조회를 통해 삭제되었는지 확인
        try {
            System.out.println(jcfU1.read(userId1));
        } catch (NoSuchElementException e) {
            System.out.println("조회할 유저가 존재하지 않습니다.");
        }


        System.out.println();


        // Channel
        // 등록
        JCFChannelService jcfC1 = new JCFChannelService();
        UUID channelId1 = jcfC1.create("실험실").getId();
        JCFChannelService jcfC2 = new JCFChannelService();

        // 조회(단건)
        try {
            System.out.println(jcfC1.read(channelId1));
        } catch (NoSuchElementException e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }
        try {
            System.out.println(jcfU2.read(UUID.randomUUID()));
        } catch (NoSuchElementException e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }

        // 조회(다건)
        System.out.println(jcfC1.readAll());
        System.out.println(jcfC2.readAll());

        // 수정
        try {
            jcfC1.updateChannelname(channelId1, "변경된 채널명");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 데이터가 존재하지 않습니다.");
        }
        try {
            jcfC1.updateChannelname(UUID.randomUUID(), "아무거나 채널명");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 데이터가 존재하지 않습니다.");
        }

        // 수정된 데이터 조회
        try {
            System.out.println(jcfC1.read(channelId1));
        } catch (NoSuchElementException e) {
            System.out.println("조회할 채널이 존재하지 않습니다.");
        }

        // 삭제
        try {
            jcfC1.delete(channelId1);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 채널이 존재하지 않습니다.");
        }
        try {
            jcfC1.delete(UUID.randomUUID());
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 채널이 존재하지 않습니다.");
        }

        // 조회를 통해 삭제되었는지 확인
        try {
            jcfC2.read(channelId1);
        } catch (NoSuchElementException e) {
            System.out.println("조회할 채널이 존재하지 않습니다.");
        }


        System.out.println();


        // Message
        // 등록
        User msgTestUser = new User();
        Channel msgTestChannel = new Channel();
        JCFMessageService jcfM1 = new JCFMessageService();
        UUID messageId1 = jcfM1.create("테스트", msgTestUser.getId(), msgTestChannel.getId()).getId();
        JCFMessageService jcfM2 = new JCFMessageService();

        // 조회(단건)
        try {
            System.out.println(jcfM1.read(messageId1));
        } catch (NoSuchElementException e) {
            System.out.println("해당 메시지가 존재하지 않습니다.");
        }
        try {
            System.out.println(jcfU2.read(UUID.randomUUID()));
        } catch (NoSuchElementException e) {
            System.out.println("해당 메시지가 존재하지 않습니다.");
        }

        // 조회(다건)
        System.out.println(jcfM1.readAll());
        System.out.println(jcfM2.readAll());

        // 수정
        try {
            jcfM1.update(messageId1, "메시지 변경");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 메시지가 존재하지 않습니다.");
        }
        try {
            jcfM1.update(UUID.randomUUID(), "아무거나 메시지");
        } catch (NoSuchElementException e) {
            System.out.println("수정할 메시지가 존재하지 않습니다.");
        }

        // 수정된 데이터 조회
        try {
            System.out.println(jcfM1.read(messageId1));
        } catch (NoSuchElementException e) {
            System.out.println("조회할 메시지가 존재하지 않습니다.");
        }

        // 삭제
        try {
            jcfM1.delete(messageId1);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 메시지가 존재하지 않습니다.");
        }
        try {
            jcfM1.delete(UUID.randomUUID());
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 메시지가 존재하지 않습니다.");
        }

        // 조회를 통해 삭제되었는지 확인
        try {
            jcfM1.read(messageId1);
        } catch (NoSuchElementException e) {
            System.out.println("조회할 메시지가 존재하지 않습니다.");
        }


        System.out.println();


        // 특정 채널에 유저 참가
        UUID userJoinTestId = jcfU1.create("참가용 테스트 유저").getId();
        UUID channelJoinTestId = jcfC1.create("참가용 테스트 채널").getId();
        try {
            jcfC1.joinUser(userJoinTestId, channelJoinTestId);
        } catch (NoSuchElementException e) {
            System.out.println("참가할 유저가 존재하지 않습니다.");
        }
        try {
            jcfU1.joinToChannel(userJoinTestId, channelJoinTestId);
        } catch (NoSuchElementException e) {
            System.out.println("참가할 채널이 존재하지 않습니다.");
        }

        // 특정 채널에 발행된 메시지 목록 조회
        try {
            System.out.println(jcfC1.readChannelMessageList(channelJoinTestId));
        } catch (NoSuchElementException e) {
            System.out.println();
        }

        // 특정 채널의 참가한 유저 목록 조회
        try {
            System.out.println(jcfC1.readChannelUserList(channelJoinTestId));
        } catch (NoSuchElementException e) {
            System.out.println("참가한 유저가 없습니다.");
        }

        // 특정 유저가 참가한 채널 목록 조회
        try {
            System.out.println(jcfU1.readUserChannelList(userJoinTestId));
        } catch (NoSuchElementException e) {
            System.out.println("참가한 채널이 없습니다.");
        }

        // 특정 유저가 발행한 메시지 리스트 조회
        try {
            System.out.println(jcfU1.readUserMessageList(userJoinTestId));
        } catch (NoSuchElementException e) {
            System.out.println("발행한 메시지가 없습니다.");
        }
    }
}