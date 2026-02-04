package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	static UserInfoDto setupUser(UserService userService, BinaryContentService binaryContentService) {
        BinaryContentCreateDto profile = new BinaryContentCreateDto("chiwawa.png".getBytes());
        BinaryContentInfoDto binaryContentInfoDto = binaryContentService.create(profile);
        UserCreateDto request = new UserCreateDto("달선", "dalsun@naver.com", "dalsun123", binaryContentInfoDto.id());
        return userService.create(request);
	}
    static UserInfoDto setupUser1(UserService userService, BinaryContentService binaryContentService) {
        BinaryContentCreateDto profile = new BinaryContentCreateDto("pomeranian.png".getBytes());
        BinaryContentInfoDto binaryContentInfoDto = binaryContentService.create(profile);
        UserCreateDto request = new UserCreateDto("달룡", "dalyong@naver.com", "dalyong123", binaryContentInfoDto.id());
        return userService.create(request);
    }
    static UserInfoDto setupUser2(UserService userService, BinaryContentService binaryContentService) {
        BinaryContentCreateDto profile = new BinaryContentCreateDto("maltipoo.png".getBytes());
        BinaryContentInfoDto binaryContentInfoDto = binaryContentService.create(profile);
        UserCreateDto request = new UserCreateDto("탄", "tan@naver.com", "tan123", binaryContentInfoDto.id());
        return userService.create(request);
    }
	static ChannelInfoDto setupChannel(ChannelService channelService, UUID ownerId) {
        PublicChannelCreateDto request = new PublicChannelCreateDto("공지방", ownerId, "모두모두 공지를 잘 확인해주세요.");
        return channelService.createPublic(request);
	}

    static ChannelInfoDto setupChannel1(ChannelService channelService, UUID ownerId, List<UUID> users) {
        PrivateChannelCreateDto request = new PrivateChannelCreateDto(ownerId, users);
        return channelService.createPrivate(request);
    }

	static MessageInfoDto setupMessage(MessageService messageService, ChannelInfoDto channel, UserInfoDto author, BinaryContentService binaryContentService) {
        List<UUID> attachments = new ArrayList<>();
        BinaryContentCreateDto img1 = new BinaryContentCreateDto("apple.png".getBytes());
        BinaryContentCreateDto img2 = new BinaryContentCreateDto("lemon.png".getBytes());
        BinaryContentCreateDto img3 = new BinaryContentCreateDto("banana.png".getBytes());
        attachments.add(binaryContentService.create(img1).id());
        attachments.add(binaryContentService.create(img2).id());
        attachments.add(binaryContentService.create(img3).id());

        MessageCreateDto request = new MessageCreateDto(author.userId(), channel.id(), "안녕하시오", attachments);
        return messageService.create(request);
	}

    static MessageInfoDto setupMessage1(MessageService messageService, ChannelInfoDto channel, UserInfoDto author, BinaryContentService binaryContentService) {
        MessageCreateDto request = new MessageCreateDto(author.userId(), channel.id(), "안녕하개", null);
        return messageService.create(request);
    }

    static MessageInfoDto setupMessage2(MessageService messageService, ChannelInfoDto channel, UserInfoDto author, BinaryContentService binaryContentService) {
        MessageCreateDto request = new MessageCreateDto(author.userId(), channel.id(), "멍망밍몽", null);
        return messageService.create(request);
    }

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
        AuthService authService = context.getBean(AuthService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);

		// 셋업
        List<UUID> users = new ArrayList<>();
        UserInfoDto dalsun = setupUser(userService, binaryContentService);
        UserInfoDto dalyong = setupUser1(userService, binaryContentService);
        UserInfoDto tan = setupUser2(userService, binaryContentService);
        users.add(dalsun.userId());
//        users.add(dalyong.userId());
        users.add(tan.userId());

        ChannelInfoDto channelPublic = setupChannel(channelService, dalsun.userId());
        ChannelInfoDto channelPrivate = setupChannel1(channelService, dalsun.userId(), users);

        MessageInfoDto dalsunMsg = setupMessage(messageService, channelPublic, dalsun, binaryContentService);
        MessageInfoDto dalyongMsg = setupMessage1(messageService, channelPublic, dalyong, binaryContentService);
        MessageInfoDto tanMsg = setupMessage2(messageService, channelPublic, tan, binaryContentService);


        // User
        //조회
        // 단건
        System.out.println(userService.findById(dalsun.userId()));
        System.out.println(userService.findById(dalyong.userId()));
        System.out.println("탄이 : " + userService.findById(tan.userId()));
        // 다건
        System.out.println(userService.findAll());

        // 수정
        UserUpdateDto dalsunUpdate = new UserUpdateDto(dalsun.userId(), "달또니", dalsun.profileId());
        userService.update(dalsunUpdate);   // 이름만 변경

        BinaryContentCreateDto lionImg = new BinaryContentCreateDto("lion.png".getBytes());
        BinaryContentInfoDto lionImgInfo = binaryContentService.create(lionImg);
        UserUpdateDto dalyongUpdate = new UserUpdateDto(dalyong.userId(), "딸롱이", lionImgInfo.id());
        userService.update(dalyongUpdate);   // 이름, 프로필 변경

        // 수정된 데이터 조회
        System.out.println("수정 후 데이터 조회\n" + userService.findAll());

        // 삭제
//        userService.delete(tan.userId());

        // 삭제된 데이터 조회
//        try{
//            System.out.println(userService.findById(tan.userId()));
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
        System.out.println("삭제 후 데이터 조회\n" + userService.findAll());


        // Channel
        // 조회
        // 단건
        System.out.println(channelService.findById(channelPrivate.id()));
        System.out.println(channelService.findById(channelPublic.id()));
        // 다건
        System.out.println("달선이 보유채널\n" + channelService.findAllByUserId(dalsun.userId()));

        // 수정, 수정된 데이터 조회
        ChannelUpdateDto publicUpdate = // 이름, 주인장 수정
                new ChannelUpdateDto(channelPublic.id(), "달멍이들의 놀이터", IsPrivate.PUBLIC, dalyong.userId());
        ChannelInfoDto publicUpdated = channelService.update(publicUpdate);
        System.out.println(publicUpdated);

//        try{
//            ChannelUpdateDto privateUpdate = // 이름, 주인장 수정
//                    new ChannelUpdateDto(channelPrivate.id(), "달멍이들의 비밀방", IsPrivate.PUBLIC, dalyong.userId());
//            ChannelInfoDto privateUpdated = channelService.update(privateUpdate);
//            System.out.println(privateUpdated);
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//        }

        // 삭제
//        channelService.delete(channelPrivate.id());

        System.out.println("채널 가입 전\n" + channelService.findAllByUserId(dalyong.userId()));
        // joinChannel
        channelService.joinChannel(dalyong.userId(), channelPrivate.id());
        channelService.joinChannel(dalyong.userId(), channelPublic.id());
        // 삭제 확인
//        System.out.println("삭제 후 채널 조회\n" + channelService.findAllByUserId(dalsun.userId()));
        System.out.println("채널 가입 후\n" + channelService.findAllByUserId(dalyong.userId()));

        // Message
        // 조회
        // 단건
        System.out.println("메시지 조회(단건)\n" + messageService.findById(dalsunMsg.id()));
        System.out.println(messageService.findById(dalyongMsg.id()));
        System.out.println(messageService.findById(tanMsg.id()));
        // 다건
        System.out.println("메시지 조회(public)\n" + messageService.findAllByChannelId(channelPublic.id()));
        System.out.println("메시지 조회(private)\n" + messageService.findAllByChannelId(channelPrivate.id()));

        // 수정
        MessageUpdateDto msgUpdate = new MessageUpdateDto(dalsunMsg.id(), "달선이라개", dalsunMsg.attachmentIds());
        System.out.println("메시지 수정\n" + messageService.update(msgUpdate));

        // 삭제
        messageService.delete(dalsunMsg.id());
        System.out.println("메시지 삭제 후 조회\n" + messageService.findAllByChannelId(channelPublic.id()));


        // AuthService 테스트
        UserLoginDto dalsunLogin = new UserLoginDto("달또니", "dalsun123");
        System.out.println("로그인 테스트\n" + authService.login(dalsunLogin));


        // ReadStatus 테스트
        System.out.println(readStatusService.findAllByUserId(dalyong.userId()));
//        ReadStatusCreateDto dalsunPrivateRS = new ReadStatusCreateDto(dalsun.userId(), channelPrivate.id());
//        System.out.println("ReadStatus 테스트\n" + readStatusService.create(dalsunPrivateRS));


        // UserStatus 테스트
        System.out.println(userStatusService.findAll());

    }
}
