package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.AutoService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.testMethod.Auth;
import com.sprint.mission.discodeit.testMethod.ChannelCrud;
import com.sprint.mission.discodeit.testMethod.MessageCrud;
import com.sprint.mission.discodeit.testMethod.UserCrud;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}

	@Bean
	CommandLineRunner run(
			UserService userService,
			ChannelService channelService,
			MessageService messageService,
			AutoService autoService
	) {
		return args -> {
			// testMethod 유틸
			UserCrud userCrud = new UserCrud(userService, channelService);
			ChannelCrud channelCrud = new ChannelCrud(userService, channelService, messageService);
			MessageCrud messageCrud = new MessageCrud(userService, channelService, messageService);
			Auth auth = new Auth(userService, autoService);

			// 중복 방지용
			String s = String.valueOf(System.currentTimeMillis() % 100000);

			String pw = "pw1234!";
			String alice = "alice_" + s;
			String bob = "bob_" + s;

			String general = "general_" + s;
			String notice = "notice_" + s;

			String[][] files1 = {{"files/a.png", "image/png"}};
			String[][] files2 = {{"files/b.txt", "text/plain"}};

			System.out.println("\n==============================");
			System.out.println("   Discodeit Smoke Test START  ");
			System.out.println("==============================\n");

			// 1) 유저
			System.out.println("[USER] 회원가입");
			userCrud.signUp(alice, alice + "@test.com", pw, "profiles/" + alice + ".png");
			userCrud.signUp(bob, bob + "@test.com", pw, "profiles/" + bob + ".png");
			userCrud.listUsers();

			// 2) 로그인
			System.out.println("\n[AUTH] 로그인");
			auth.login(alice, pw);
			auth.login(bob, pw);
			userCrud.checkUserStatus(alice, pw);
			userCrud.checkUserStatus(bob, pw);

			// 3) 공개 채널 (※ PRIVATE 만들기 전에 PUBLIC 먼저: createPublic 중복검사 NPE 회피용)
			System.out.println("\n[CHANNEL-PUBLIC] 공개 채널 생성");
			channelCrud.createPublicChannel(general, "general room");
			channelCrud.createPublicChannel(notice, "notice room");
			channelCrud.listChannel();

			// 4) 채널 가입
			System.out.println("\n[CHANNEL] 채널 가입");
			userCrud.joinChannel(alice, pw, general);
			userCrud.joinChannel(bob, pw, general);
			channelCrud.listChannelByUser(alice);
			channelCrud.listChannelByUser(bob);

			// 5) 메시지 작성/수정/삭제 (공개 채널)
			System.out.println("\n[MESSAGE] 공개 채널 메시지 작성");
			messageCrud.sendMessage(alice, general, "hello from " + alice, files1);
			messageCrud.sendMessage(bob, general, "hello from " + bob, files2);

			System.out.println("\n[MESSAGE] 공개 채널 메시지 목록");
			channelCrud.listAllMessage(general);

			String lastMsgShortId = lastMessageShortId(channelService, general);
			System.out.println("\n[MESSAGE] 마지막 메시지 수정 (id=" + lastMsgShortId + ")");
			messageCrud.updateMessage(general, lastMsgShortId, "edited!", files1);

			System.out.println("\n[MESSAGE] 수정 후 메시지 목록");
			channelCrud.listAllMessage(general);

			System.out.println("\n[MESSAGE] 마지막 메시지 삭제 (id=" + lastMsgShortId + ")");
			messageCrud.deleteMessage(general, lastMsgShortId);

			System.out.println("\n[MESSAGE] 삭제 후 메시지 목록");
			channelCrud.listAllMessage(general);

			// 6) 익명(PRIVATE) 채널 생성 + 메시지
			System.out.println("\n[CHANNEL-PRIVATE] 익명 채널 생성");
			channelCrud.createPrivateChannel(List.of(alice, bob));
			String privateServerId = latestPrivateServerId(channelService);

			System.out.println("\n[MESSAGE] 익명 채널 메시지 작성 (serverId=" + privateServerId + ")");
			messageCrud.sendMessage(alice, privateServerId, "hi bob (private)", files1);
			messageCrud.sendMessage(bob, privateServerId, "hi alice (private)", files2);

			System.out.println("\n[MESSAGE] 익명 채널 메시지 목록");
			channelCrud.listAllMessage(privateServerId);

			// 7) 로그아웃
			System.out.println("\n[AUTH] 로그아웃");
			auth.logout(alice);
			auth.logout(bob);

			System.out.println("\n==============================");
			System.out.println("   Discodeit Smoke Test END    ");
			System.out.println("==============================\n");
		};
	}

	// 채널의 마지막 메시지(UUID) 앞 8자리
	private static String lastMessageShortId(ChannelService channelService, String nameOrId) {
		Channel channel = channelService.checkChannel(nameOrId);
		if (channel.getMessageIds().isEmpty()) {
			throw new IllegalStateException("채널에 메시지가 없습니다.");
		}
		UUID last = channel.getMessageIds().get(channel.getMessageIds().size() - 1);
		return last.toString().substring(0, 8);
	}

	// 가장 최근 PRIVATE 채널 serverId
	private static String latestPrivateServerId(ChannelService channelService) {
		return channelService.findAllPrivateChannels().stream()
				.max(Comparator.comparing(ChannelDto.ChannelResponsePrivate::lastMessageAt))
				.map(ChannelDto.ChannelResponsePrivate::serverId)
				.orElseThrow(() -> new IllegalStateException("PRIVATE 채널이 없습니다."));
	}
}
