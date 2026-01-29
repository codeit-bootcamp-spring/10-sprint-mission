//package com.sprint.mission;
//
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import com.sprint.mission.discodeit.service.file.FileUserService;
//
//import java.util.NoSuchElementException;
//import java.util.UUID;
//
//public class JavaApplication {
//    public static void main(String[] args) {
//
////        // User
////        // 등록
////        JCFUserService jcfU1 = new JCFUserService();
////        UUID userId1 = jcfU1.create("홍길동").getId();
////        JCFUserService jcfUserEmpty = new JCFUserService();
////
////        // 조회(단건)
////        try {
////            System.out.println(jcfU1.find(userId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 유저가 존재하지 않습니다.");
////        }
////        try {
////            System.out.println(jcfUserEmpty.find(UUID.randomUUID()));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 유저가 존재하지 않습니다.");
////        }
////
////        // 조회(다건)
////        System.out.println(jcfU1.findAll());
////        System.out.println(jcfUserEmpty.findAll());
////
////        // 수정
////        try {
////            jcfU1.updateUser(userId1, "일이삼");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 유저가 존재하지 않습니다.");
////        }
////        try {
////            jcfU1.updateUser(UUID.randomUUID(), "아무거나 유저명");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 유저가 존재하지 않습니다.");
////        }
////
////        // 수정된 데이터 조회
////        try {
////            System.out.println(jcfU1.find(userId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 유저가 존재하지 않습니다.");
////        }
////
////        // 삭제
////        try {
////            jcfU1.delete(userId1);
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 유저가 존재하지 않습니다.");
////        }
////        try {
////            jcfU1.delete(UUID.randomUUID());
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 유저가 존재하지 않습니다.");
////        }
////
////        // 조회를 통해 삭제되었는지 확인
////        try {
////            System.out.println(jcfU1.find(userId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 유저가 존재하지 않습니다.");
////        }
////
////
////        System.out.println();
////
////
////        // Channel
////        // 등록
////        JCFChannelService jcfC1 = new JCFChannelService();
////        UUID channelId1 = jcfC1.create("실험실").getId();
////        JCFChannelService jcfC2 = new JCFChannelService();
////
////        // 조회(단건)
////        try {
////            System.out.println(jcfC1.find(channelId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 채널이 존재하지 않습니다.");
////        }
////        try {
////            System.out.println(jcfUserEmpty.find(UUID.randomUUID()));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 채널이 존재하지 않습니다.");
////        }
////
////        // 조회(다건)
////        System.out.println(jcfC1.findAll());
////        System.out.println(jcfC2.findAll());
////
////        // 수정
////        try {
////            jcfC1.updateChannelname(channelId1, "변경된 채널명");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 데이터가 존재하지 않습니다.");
////        }
////        try {
////            jcfC1.updateChannelname(UUID.randomUUID(), "아무거나 채널명");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 데이터가 존재하지 않습니다.");
////        }
////
////        // 수정된 데이터 조회
////        try {
////            System.out.println(jcfC1.find(channelId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 채널이 존재하지 않습니다.");
////        }
////
////        // 삭제
////        try {
////            jcfC1.delete(channelId1);
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 채널이 존재하지 않습니다.");
////        }
////        try {
////            jcfC1.delete(UUID.randomUUID());
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 채널이 존재하지 않습니다.");
////        }
////
////        // 조회를 통해 삭제되었는지 확인
////        try {
////            jcfC2.find(channelId1);
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 채널이 존재하지 않습니다.");
////        }
////
////
////        System.out.println();
////
////
////        // Message
////        // 등록
////        UUID msgTestUser = jcfU1.create("메시지 테스트용 유저").getId();
////        UUID msgTestChannel = jcfC1.create("메시지 테스트용 채널").getId();
////        JCFMessageService jcfM1 = new JCFMessageService(jcfC1, jcfU1);
////        UUID messageId1 = UUID.randomUUID();
////        try {
////            messageId1 = jcfM1.create("테스트", msgTestUser, msgTestChannel).getId();
////        } catch (NoSuchElementException e) {
////            System.out.println("메시지를 생성할 수 없습니다.");
////        }
////        JCFMessageService jcfMessageEmpty = new JCFMessageService();
////
////        // 조회(단건)
////        try {
////            System.out.println(jcfM1.find(messageId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 메시지가 존재하지 않습니다.");
////        }
////        try {
////            System.out.println(jcfUserEmpty.find(UUID.randomUUID()));
////        } catch (NoSuchElementException e) {
////            System.out.println("해당 메시지가 존재하지 않습니다.");
////        }
////
////        // 조회(다건)
////        System.out.println(jcfM1.findAll());
////        System.out.println(jcfMessageEmpty.findAll());
////
////        // 수정
////        try {
////            jcfM1.updateMessageData(messageId1, "메시지 변경");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 메시지가 존재하지 않습니다.");
////        }
////        try {
////            jcfM1.updateMessageData(UUID.randomUUID(), "아무거나 메시지");
////        } catch (NoSuchElementException e) {
////            System.out.println("수정할 메시지가 존재하지 않습니다.");
////        }
////
////        // 수정된 데이터 조회
////        try {
////            System.out.println(jcfM1.find(messageId1));
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 메시지가 존재하지 않습니다.");
////        }
////
////        // 삭제
////        try {
////            jcfM1.delete(messageId1);
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 메시지가 존재하지 않습니다.");
////        }
////        try {
////            jcfM1.delete(UUID.randomUUID());
////        } catch (NoSuchElementException e) {
////            System.out.println("삭제할 메시지가 존재하지 않습니다.");
////        }
////
////        // 조회를 통해 삭제되었는지 확인
////        try {
////            jcfM1.find(messageId1);
////        } catch (NoSuchElementException e) {
////            System.out.println("조회할 메시지가 존재하지 않습니다.");
////        }
////
////
////        System.out.println();
////
////
////        // 특정 채널에 유저 참가
////        UUID userJoinTestId = jcfU1.create("참가용 테스트 유저").getId();
////        UUID channelJoinTestId = jcfC1.create("참가용 테스트 채널").getId();
////        try {
////            jcfC1.joinUser(userJoinTestId, channelJoinTestId);
////        } catch (NoSuchElementException e) {
////            System.out.println("참가할 유저가 존재하지 않습니다.");
////        }
////
////        // 특정 채널에 발행된 메시지 목록 조회
////        try {
////            System.out.println(jcfM1.readChannelMessageList(channelJoinTestId));
////        } catch (NoSuchElementException e) {
////            System.out.println();
////        }
////
////        // 특정 채널의 참가한 유저 목록 조회
////        try {
////            System.out.println(jcfU1.readChannelUserList(channelJoinTestId));
////        } catch (NoSuchElementException e) {
////            System.out.println("참가한 유저가 없습니다.");
////        }
////
////        // 특정 유저가 참가한 채널 목록 조회
////        try {
////            System.out.println(jcfC1.readUserChannelList(userJoinTestId));
////        } catch (NoSuchElementException e) {
////            System.out.println("참가한 채널이 없습니다.");
////        }
////
////        // 특정 유저가 발행한 메시지 리스트 조회
////        try {
////            System.out.println(jcfM1.readUserMessageList(userJoinTestId));
////        } catch (NoSuchElementException e) {
////            System.out.println("발행한 메시지가 없습니다.");
////        }
//
//
//        // 직렬화 테스트
//
//        // 직렬화 테스트
//        FileUserService fUserService= new FileUserService();
//        UUID fileUId = fUserService.create("황민재").getId();
//        System.out.println(fileUId);
//        fUserService.create("허문수");
//        fUserService.create("홍길동");
//
//        // find 테스트
//        try {
//            System.out.println(fUserService.find(fileUId));
//        } catch (NoSuchElementException e) {
//            System.out.println("유저가 없습니다.");
//        }
//
//        // update 테스트
//        try {
//            System.out.println(fUserService.updateUserName(fileUId, "심석수"));
//        } catch (Exception e) {
//            System.out.println("이름 변경 실패");
//        }
//
//        // findAll 테스트
//        try {
//            System.out.println(fUserService.findAll());
//        } catch (Exception e) {
//            System.out.println("역직렬화 리스트 반환 실패");
//        }
//
//        // delete 테스트
//        try {
//            fUserService.delete(fileUId);
//        } catch (Exception e) {
//            System.out.println("삭제 실패");
//        }
//
//        // Basic*service 테스트
//        // Basic User
//        BasicUserService basicJCFUserService = new BasicUserService(new JCFUserRepository(), new FileUserStatusRepository());
//        BasicUserService basicFileUserService = new BasicUserService(new FileUserRepository(), new FileUserStatusRepository());
//
//        // Basic Channel
//        BasicChannelService basicJCFChannelService = new BasicChannelService(new JCFChannelRepository());
//        BasicChannelService basicFileChannelService = new BasicChannelService(new FileChannelRepository());
//
//        // Basic Message
//        BasicMessageService basicJCFMessageService = new BasicMessageService(new JCFMessageRepository(), basicJCFChannelService, basicJCFUserService);
//        BasicMessageService basicFileMessageService = new BasicMessageService(new FileMessageRepository(), basicFileChannelService, basicFileUserService);
//
//        // create
//        UUID jcfUserId = basicJCFUserService.create("황민재JCF").getId();
//        UUID fileUserId = basicFileUserService.create("황민재File").getId();
//        UUID jcfChannelId = basicJCFChannelService.create("배이직 JCF 채널").getId();
//        UUID fileChannelId = basicFileChannelService.create("배이직 File 채널").getId();
//        UUID jcfMessageId = basicJCFMessageService.create("JCF메시지 입니다.", jcfUserId, jcfChannelId).getId();
//        UUID fileMessageId = basicFileMessageService.create("File메시지 입니다.", fileUserId, fileChannelId).getId();
//
//        System.out.println();
//
//        // find
//        try {
//            System.out.println(basicJCFUserService.find(jcfUserId));
//            System.out.println(basicFileUserService.find(fileUserId));
//            System.out.println(basicJCFChannelService.read(jcfChannelId));
//            System.out.println(basicFileChannelService.read(fileChannelId));
//            System.out.println(basicJCFMessageService.read(jcfMessageId));
//            System.out.println(basicFileMessageService.read(fileMessageId));
//        } catch (Exception e) {
//            System.out.println("find 실패");
//        }
//
//        System.out.println();
//
//        // findAll
//        try {
//            System.out.println("Basic*User");
//            System.out.println(basicJCFUserService.findAll());
//            System.out.println(basicFileUserService.findAll());
//            System.out.println();
//            System.out.println("Basic*Channel");
//            System.out.println(basicJCFChannelService.readAll());
//            System.out.println(basicFileChannelService.readAll());
//            System.out.println();
//            System.out.println("Basic*Message");
//            System.out.println(basicJCFMessageService.readAll());
//            System.out.println(basicFileMessageService.readAll());
//        } catch (Exception e) {
//            System.out.println("findAll 실패");
//        }
//
//        System.out.println();
//
//        // update
//        try {
//            basicJCFUserService.updateUser(jcfUserId, "변경된 황민재JCF");
//            System.out.println(basicJCFUserService.find(jcfUserId));
//            basicFileUserService.updateUser(fileUserId, "변경된 황민재File");
//            System.out.println(basicFileUserService.find(fileUserId));
//            basicJCFChannelService.updateChannelname(jcfChannelId, "변경된 배이직 JCF 채널");
//            System.out.println(basicJCFChannelService.read(jcfChannelId));
//            basicFileChannelService.updateChannelname(fileChannelId, "변경된 배이직 File 채널");
//            System.out.println(basicFileChannelService.read(fileChannelId));
//            basicJCFMessageService.updateMessageData(jcfMessageId, "변경된 JCF메시지 입니다.");
//            System.out.println(basicJCFMessageService.read(jcfMessageId));
//            basicFileMessageService.updateMessageData(fileMessageId, "변경된 File메시지 입니다.");
//            System.out.println(basicFileMessageService.read(fileMessageId));
//        } catch (Exception e) {
//            System.out.println("update 실패");
//        }
//
//        System.out.println();
//
//        // delete
//        basicJCFUserService.delete(jcfUserId);
//        basicFileUserService.delete(fileUserId);
//        basicJCFChannelService.delete(jcfChannelId);
//        basicFileChannelService.delete(fileChannelId);
//        basicJCFMessageService.delete(jcfMessageId);
//        basicFileMessageService.delete(fileMessageId);
//        try {
//            System.out.println(basicJCFUserService.find(jcfUserId));
//        } catch (Exception e) {
//            System.out.println("jcfUserId 검색 실패");
//        }
//        try {
//            System.out.println(basicFileUserService.find(fileUserId));
//        } catch (Exception e) {
//            System.out.println("fileUserId 검색 실패");
//        }
//        try {
//            System.out.println(basicJCFChannelService.read(jcfChannelId));
//        } catch (Exception e) {
//            System.out.println("jcfChannelId검색 실패");
//        }
//        try {
//            System.out.println(basicFileChannelService.read(fileChannelId));
//        }catch (Exception e) {
//            System.out.println("fileChannelId검색 실패");
//        }
//        try {
//            System.out.println(basicJCFMessageService.read(jcfMessageId));
//        } catch (Exception e) {
//            System.out.println("jcfMessageId검색 실패");
//        }
//        try {
//            System.out.println(basicFileMessageService.read(fileMessageId));
//        } catch (Exception e) {
//            System.out.println("fileMessageId검색 실패");
//        }
//    }
//}