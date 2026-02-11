//package com.sprint.mission.discodeit;
//
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.exception.MessageNotFoundException;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import com.sprint.mission.discodeit.service.file.FileChannelService;
//import com.sprint.mission.discodeit.service.file.FileMessageService;
//import com.sprint.mission.discodeit.service.file.FileUserService;
//import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.service.jcf.JCFUserService;
//import com.sprint.mission.discodeit.view.BasicChannelMessageView;
//import com.sprint.mission.discodeit.view.ChannelMessageView;
//import com.sprint.mission.discodeit.view.ChannelView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class JavaApplication {
//    public static void main(String[] args) {
//
////            // JCF기반
////            UserRepository userRepository = new JCFUserRepository();
////            UserService userService = new BasicUserService(userRepository);
////
////            ChannelRepository channelRepository = new JCFChannelRepository();
////            ChannelService channelService = new BasicChannelService(channelRepository, userRepository);
////
////            MessageRepository messageRepository = new JCFMessageRepository(userService, channelService);
////            MessageService messageService = new BasicMessageService(channelRepository, userRepository, messageRepository);
//        // FILE기반
//        FileUserRepository fileUserRepository = new FileUserRepository();
//        fileUserRepository.resetUserFile();
//        UserService userService = new BasicUserService(fileUserRepository);
//        FileChannelRepository fileChannelRepository = new FileChannelRepository(userService);
//        fileChannelRepository.resetChannelFile();
//        ChannelService channelService = new BasicChannelService(fileChannelRepository, fileUserRepository);
//        FileMessageRepository fileMessageRepository = new FileMessageRepository(userService, channelService);
//        fileMessageRepository.resetMessageFile();
//        MessageService messageService = new BasicMessageService(fileChannelRepository, fileUserRepository, fileMessageRepository, userService,  channelService);
//
//        // 유저 생성
//        String[][] userData = {
//                {"김코딩", "kim@test.com"},
//                {"이코딩", "lee@test.com"},
//                {"박코딩", "park@test.com"},
//                {"최코딩", "choi@test.com"},
//                {"정코딩", "jung@test.com"},
//                {"안코딩", "ahn@test.com"}
//        };
//
//        List<User> users = new ArrayList<>();
//        for (String[] data : userData) users.add(userService.createUser(data[0], data[1]));
//
//        //유저 단건 출력
//        System.out.println("[유저 출력(단건)]");
//        User findUser = userService.findUser(users.get(0).getId());
//        System.out.println(findUser);
//        System.out.println("개인 식별자(Id): " + findUser.getId());
//        System.out.println();
//
//        //유저 다건 출력
//        System.out.println("[유저 출력(다건)]");
//        for(User allUser : userService.findAllUser()){
//            System.out.println(allUser);
//            System.out.println();
//        }
//
//        //유저 수정 후 출력
//        System.out.println("[유저 출력(수정)]");
//        User updateUser = userService.updateUser(users.get(1).getId(), "이코딩_수정", "lee_update@test.com");
//        System.out.println(updateUser);
//        System.out.println();
//
//        //유저 삭제 후 출력
//        System.out.println("[유저 출력(삭제)]");
//        User deleteUser = userService.deleteUser(users.get(0).getId());
//
//        for(User allUser : userService.findAllUser()){
//            System.out.println(allUser);
//            System.out.println();
//        }
//
//        // 채널 생성
//        String[] channelData = {"채널1", "채널2", "채널3", "채널4"};
//        for (String name : channelData) {
//            channelService.createChannel(name);
//        }
//
//        List<Channel> channels = channelService.findAllChannel();
//
//        // 채널 멤버 추가
//        channelService.userAddChannel(
//                channels.get(0).getId(),
//                users.get(0).getId()
//        );
//        channelService.userAddChannel(
//                channels.get(0).getId(),
//                users.get(1).getId()
//        );
//        channelService.userAddChannel(
//                channels.get(1).getId(),
//                users.get(2).getId()
//        );
//        channelService.userAddChannel(
//                channels.get(2).getId(),
//                users.get(3).getId()
//        );
//        channelService.userAddChannel(
//                channels.get(3).getId(),
//                users.get(4).getId()
//        );
//        channelService.userAddChannel(
//                channels.get(1).getId(),
//                users.get(5).getId()
//        );
//
//        //채널 단건 출력
//        System.out.println("[채널 출력 이름 (단건)]");
//        Channel findOneChannel = channelService.findChannel(channels.get(0).getId());
//        System.out.println(ChannelView.viewChannel(findOneChannel));
//        System.out.println();
//
//        //채널 다건 출력
//        System.out.println("[채널 출력(다건)]");
//        for(Channel allChannel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(allChannel));
//            System.out.println();
//        }
//
//        //채널 이름 수정
//        System.out.println("[채널 이름 수정]");
//        Channel updateChannel = channelService.nameUpdateChannel(channels.get(1).getId(), "채널2_수정");
//        System.out.println(ChannelView.viewChannel(updateChannel));
//        System.out.println();
//
//        //채널 삭제
//        System.out.println("[채널 삭제]");
//        Channel deleteChannel = channelService.deleteChannel(channels.get(0).getId());
//        for(Channel allChannel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(allChannel));
//            System.out.println();
//        }
//
//        // 메시지 생성
//        Message m1 =messageService.createMessage(
//                users.get(0).getId(), channels.get(0).getId(), "안녕하세요!");
//        Message m2 =messageService.createMessage(
//                users.get(1).getId(), channels.get(0).getId(), "반갑습니다!");
//        Message m3 = messageService.createMessage(
//                users.get(2).getId(), channels.get(1).getId(), "채널2 첫 메시지");
//
//        // 채널별 메시지 출력
//        System.out.println("[채널별 메시지 출력]");
//        for(Channel channel : channelService.findAllChannel()) {
//            System.out.println(BasicChannelMessageView.viewMessage(channel, messageService));
//            System.out.println();
//        }
//
//        System.out.println();
//
//        // 전체 메시지 출력
//        System.out.println("[서버 전체 메시지]");
//        System.out.println(BasicChannelMessageView.viewAllMessages(messageService));
//        System.out.println();
//
//        // 메시지 수정 후 출력
//        System.out.println("[메시지 수정]");
//        Message updatedMessage = messageService.updateMessage(m2.getId(), "메시지 수정");
//        System.out.println(BasicChannelMessageView.viewSingleMessage(updatedMessage));
//
//        // 메시지 삭제
//        messageService.deleteMessage(m1.getId());
//
//        // 채널별 메시지 출력 (삭제 후)
//        System.out.println("[채널별 메시지 출력 (삭제 후)]");
//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(BasicChannelMessageView.viewMessage(channel, messageService));
//            System.out.println();
//        }
//
//        // 특정 유저가 참여중인 채널 목록 조회
//        System.out.println("[특정유저가 참여중인 채널]");
//        User user1 = users.get(0); // 김코딩
//        Channel channel = channelService.findByUserChannel(user1.getId());
//        System.out.println(user1.getUserName() + "이 참여한 채널: " + channel.getChannelName());
//        System.out.println();
//
//        // 특정 유저가 발행한 메시지 목록 조회
//        System.out.println("[특정 유저가 발행한 메시지 목록]");
//        User user2 = users.get(2); // 박코딩
//        try {
//            List<Message> messages = messageService.findAllByUserMessage(user2.getId());
//
//            System.out.println("[" + user2.getUserName() + "]이 발행한 메시지 목록");
//            for (Message message : messages) {
//                // 단순 toString 대신 포맷된 메시지 출력
//                System.out.println(BasicChannelMessageView.viewSingleMessage(message));
//            }
//        } catch (MessageNotFoundException e) {
//            System.out.println("[" + user2.getUserName() + "]이 발행한 메시지가 없습니다.");
//        }
//
//        System.out.println();
//
//        // 특정 채널 참가자 목록
//        System.out.println("[특정 채널 참가자 목록]");
//        Channel channel3 = channels.get(1); // 1채널
//        String members = channelService.findAllUserInChannel(channel3.getId());
//        System.out.println("[" + channel3.getChannelName() + "]" + "에 있는 멤버: " + members);
//    }
//}
//
