
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.repository.*;

import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicChatCoordinator;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.utils.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import static java.nio.file.Files.deleteIfExists;



@SpringBootApplication
@RequiredArgsConstructor
public class DiscodeitApplication implements CommandLineRunner {

//	public static void main(String[] args) throws IOException {
        //  실행 시 기존 데이터 파일 삭제 (테스트용 초기화)
//        deleteIfExists(Path.of("users.dat"));
//        deleteIfExists(Path.of("channels.dat"));
//        deleteIfExists(Path.of("messages.dat"));
//        ApplicationContext context =
//                SpringApplication.run(DiscodeitApplication.class, args);
//		System.out.println("===========스프링 빈 체크!!!!!=============");
//		for(String beanName : context.getBeanDefinitionNames()) {
//			System.out.println(beanName);
//		}
//		System.out.println("========빈 체크 종료===============");

//		// Spring Context에서 빈 꺼내기.
//		UserService userService = context.getBean(UserService.class);
//		ChannelService channelService = context.getBean(ChannelService.class);
//		MessageService messageService = context.getBean(MessageService.class);
//		BasicChatCoordinator coordinator = context.getBean(BasicChatCoordinator.class);
//
//		System.out.println("=======테스트 시작!!!!=======");
//
//
//// 유저/채널 2개 생성
//		User u1 = userService.createUser("종인", "jongin", "jongin98@naver.com", "061116");
//		User u2 = userService.createUser("민수", "minsu" , "minsu97@naver.com", "192302");
//
//		Channel general = channelService.createChannel("general");
//		Channel music = channelService.createChannel("music");
//
//// u2는 채널 2개 참가
//		coordinator.joinChannel(u2.getId(), general.getId());
//		coordinator.joinChannel(u2.getId(), music.getId());
//
//// u2 메시지 3개 (general 2, music 1)
//		Message g1 = coordinator.sendMessage(u2.getId(), general.getId(), "g-hello1");
//		Message g2 = coordinator.sendMessage(u2.getId(), general.getId(), "g-hello2");
//		Message m1 = coordinator.sendMessage(u2.getId(), music.getId(), "m-hello1");
//
//		System.out.println("=== BEFORE UPDATE (memory) ===");
//		System.out.println(u1.getAlias() + u1.getUserName());
//		printCore(u2.getId(), general.getId(), music.getId(), g1.getId(), g2.getId(), m1.getId(),
//				userService, channelService, messageService);
//
//		// updateUserCascade
//		coordinator.updateUserCascade(u2.getId(), "민수(수정)", "minsu2");
//
//		System.out.println(" AFTER updateUserCascade (memory) ");
//		printCore(u2.getId(), general.getId(), music.getId(), g1.getId(), g2.getId(), m1.getId(),
//				userService, channelService, messageService);
//
//		// updateChannelCascade 검증 <music -> music-room>
//		coordinator.updateChannelCascade(music.getId(), "music-room");
//
//		System.out.println(" AFTER updateChannelCascade (memory) ");
//		printCore(u2.getId(), general.getId(), music.getId(), g1.getId(), g2.getId(), m1.getId(),
//				userService, channelService, messageService);
//
////		// RELOAD CHECK
////		ApplicationContext reloadContext =
////				SpringApplication.run(DiscodeitApplication.class, args);
////		UserService userService2 = reloadContext.getBean(UserService.class);
////		ChannelService channelService2 = reloadContext.getBean(ChannelService.class);
////		MessageService messageService2 = reloadContext.getBean(MessageService.class);
////
////
////		System.out.println("AFTER UPDATE (reload from file)");
////		printCore(u2.getId(), general.getId(), music.getId(), g1.getId(), g2.getId(), m1.getId(),
////				userService2, channelService2, messageService2);
////
////		System.out.println(" END ");
//	}
//
//
//
//
//	private static void printCore(
//			UUID u2Id, UUID generalId, UUID musicId,
//			UUID g1Id, UUID g2Id, UUID m1Id,
//			UserService userService, ChannelService channelService, MessageService messageService
//	)
//	{
//		User u2 = userService.findUserById(u2Id);
//		Channel general = channelService.findChannelById(generalId);
//		Channel music = channelService.findChannelById(musicId);
//
//		Message g1 = messageService.getMessageById(g1Id);
//		Message g2 = messageService.getMessageById(g2Id);
//		Message m1 = messageService.getMessageById(m1Id);
//
//		System.out.println("u2 별명 = " + u2.getAlias());
//		System.out.println("general 참가자 = " + general.getParticipants());
//		System.out.println("music 참가자   = " + music.getParticipants());
//
//		System.out.println("g1: " + g1.getSender().getAlias() + " -> " + g1.getChannel().getChannelName() + " | " + g1.getMessageContent());
//		System.out.println("g2: " + g2.getSender().getAlias() + " -> " + g2.getChannel().getChannelName() + " | " + g2.getMessageContent());
//		System.out.println("m1: " + m1.getSender().getAlias() + " -> " + m1.getChannel().getChannelName() + " | " + m1.getMessageContent());
//		System.out.println();
//	}
        // =========================
        // Repository (JCF 기반)
        // =========================
            private final UserService userService;
            private final ChannelService channelService;
            private final MessageService messageService;

            public static void main(String[] args){
                    SpringApplication.run(DiscodeitApplication.class, args);
            }

                    @Override
                    public void run (String...args){
                            System.out.println("=== [Spring + JCF] 간단 테스트 시작 ===");

                            // 1) 유저 생성
                            UserResponse u1 = userService.createUser(new UserCreateRequest(
                                    "홍길동", "gildong", "gildong@test.com", "1234", null
                            ));
                            UserResponse u2 = userService.createUser(new UserCreateRequest(
                                    "김철수", "chulsoo", "chulsoo@test.com", "1234", null
                            ));
                            System.out.println("[유저 생성] " + u1);
                            System.out.println("[유저 생성] " + u2);

                            // 2) 채널 생성
                            var ch1 = channelService.createChannel("general");
                            System.out.println("[채널 생성] " + ch1.getId() + " / " + ch1.getChannelName());

                            // 3) 채널 참가/퇴장 (채널 서비스에 join/leave를 옮겨두신 버전 기준)
                            channelService.joinChannel(u1.id(), ch1.getId());
                            channelService.joinChannel(u2.id(), ch1.getId());
                            System.out.println("[채널 참가] participants=" + ch1.getParticipants().size());

                            // 4) 메세지 전송 (Message는 senderId/channelId만 들고 있는 구조 기준)
                            var m1 = messageService.createMessage("안녕하세요!", u1.id(), ch1.getId());
                            var m2 = messageService.createMessage("반가워요!", u2.id(), ch1.getId());
                            System.out.println("[메세지 생성] " + m1.getId());
                            System.out.println("[메세지 생성] " + m2.getId());

                            // 5) 조회 예시: senderId로 필터링 (ID-only 기준)
                            System.out.println("[홍길동이 보낸 메세지 수] " +
                                    messageService.getAllMessagesResponse().stream().filter(m -> m.senderId().equals(u1.id())).count());

                            // 6) 메세지 삭제 (ID-only + messageIds 정리 버전 deleteMessage 기준)
                            messageService.deleteMessage(m1.getId());
                            System.out.println("[메세지 삭제] " + m1.getId());

                            // 7) 유저 업데이트/삭제도 원하면 여기서 호출 가능
                            // userService.updateUser(...)
                            // userService.deleteUser(u2.id());

                            System.out.println("=== [Spring + JCF] 테스트 종료 ===");

            }


}

