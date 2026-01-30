
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


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
            private final ReadStatusRepository readStatusRepository;

            public static void main(String[] args){
                    SpringApplication.run(DiscodeitApplication.class, args);
            }

            @Override
            public void run (String...args) {
                System.out.println("=== [Spring + JCF] 간단 테스트 시작 ===");

                // 1) 유저 생성
                UserResponse u1 = userService.createUser(new UserCreateRequest(
                        "홍길동", "gildong", "gildong@test.com", "1234", null
                ));
                UserResponse u2 = userService.createUser(new UserCreateRequest(
                        "김철수", "chulsoo", "chulsoo@test.com", "1234", null
                ));
                System.out.println("[유저 생성] " + u1.userName());
                System.out.println("[유저 생성] " + u2.userName());

                // 2) Public 채널 생성
                System.out.println("====public Channel 생성====");
                Channel publicChannel = channelService.createPublicChannel(
                        new PublicChannelCreateRequest("general[Public]")
                );

                channelService.joinChannel(u1.id(), publicChannel.getId());
                channelService.joinChannel(u2.id(), publicChannel.getId());

                System.out.println("\n[PUBLIC 채널]");
                System.out.println("id   = " + publicChannel.getId());
                System.out.println("name = " + publicChannel.getChannelName());
                System.out.println("participants = " + publicChannel.getParticipants().size());

                // Private 채널 생성
                Channel privateChannel = channelService.createPrivateChannel(
                        new PrivateChannelCreateRequest(List.of(u1.id(), u2.id()))
                );
                System.out.println("\n[PRIVATE 채널]");
                System.out.println("id   = " + privateChannel.getId());
                System.out.println("name = " + privateChannel.getChannelName());
                System.out.println("participants = " +
                        privateChannel.getParticipants().stream()
                                .map(u -> u.getId().toString())
                                .toList()
                );

                // ReadStatus 확인
                List<ReadStatus> readStatuses =
                        readStatusRepository.findStatusByChannelId(privateChannel.getId());

                System.out.println("\n[PRIVATE 채널 ReadStatus]");
                System.out.println("count = " + readStatuses.size());

                for(ReadStatus readStatus : readStatuses){
                    System.out.println(
                            "userId = " + readStatus.getUserId()
                            + "| channelId = " + readStatus.getChannelId()
                            + " | lastReadAt + " + readStatus.getLastReadAt()
                    );
                }

                System.out.println("\n ======== 테스트 종료 =========");



            }


}

