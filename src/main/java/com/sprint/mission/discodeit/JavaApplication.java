//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//import java.util.UUID;
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        User user = userService.create("woody", StatusType.AWAY);
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService, UUID ownerId) {
//        Channel channel = channelService.create("공지", IsPrivate.PUBLIC, ownerId);
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.create(author.getId(), channel.getId(), "안녕하세요.");
//        System.out.println("메시지 생성: " + message.getContent());
//    }
//
//    public static void main(String[] args) {
//        // 서비스 초기화
//        // TODO Basic*Service 구현체를 초기화하세요.
////        UserRepository userRepository = new JCFUserRepository();
////        ChannelRepository channelRepository = new JCFChannelRepository();
////        MessageRepository messageRepository = new JCFMessageRepository();
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//        UserStatusRepository userStatusRepository = new FileUserStatusRepository();
//        UserService userService = new BasicUserService(userRepository, channelRepository, messageRepository, userStatusRepository);
//        ChannelService channelService = new BasicChannelService(userService, userRepository, channelRepository, messageRepository);
//        MessageService messageService = new BasicMessageService(userService, channelService, messageRepository, channelRepository, userStatusRepository);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService, user.getId());
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package com.sprint.mission.discodeit;
////
////import com.sprint.mission.discodeit.entity.*;
////import com.sprint.mission.discodeit.repository.ChannelRepository;
////import com.sprint.mission.discodeit.repository.MessageRepository;
////import com.sprint.mission.discodeit.repository.UserRepository;
////import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
////import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
////import com.sprint.mission.discodeit.repository.file.FileUserRepository;
////import com.sprint.mission.discodeit.service.file.FileChannelService;
////import com.sprint.mission.discodeit.service.file.FileMessageService;
////import com.sprint.mission.discodeit.service.file.FileUserService;
////
////import java.util.List;
////import java.util.UUID;
////
////public class JavaApplication {
////    public static void main(String[] args) {
////        UserRepository userRepository = new FileUserRepository("User.ser");
////        MessageRepository messageRepository = new FileMessageRepository("Message.ser");
////        ChannelRepository channelRepository = new FileChannelRepository("Channel.ser");
////
////        FileUserService userService = new FileUserService(userRepository, channelRepository, messageRepository);
////        FileChannelService channelService = new FileChannelService(userService, userRepository, channelRepository, messageRepository);
////        FileMessageService messageService = new FileMessageService(userService, channelService, channelRepository, messageRepository);
////        userService.clear();
////        channelService.clear();
////        messageService.clear();
////
////        // 1. 초기 설정 및 초기화
//////        UserRepository userRepository = new JCFUserRepository();
//////        MessageRepository messageRepository = new JCFMessageRepository();
//////        ChannelRepository channelRepository = new JCFChannelRepository();
//////
//////        JCFUserService userService = new JCFUserService(userRepository, channelRepository, messageRepository);
//////        JCFChannelService channelService = new JCFChannelService(userService, channelRepository, messageRepository);
//////        JCFMessageService messageService = new JCFMessageService(userService, channelService, messageRepository);
////
////        System.out.println("=== 1. 사용자 생성 및 중복 테스트 ===");
////        User user1 = userService.create("달선", UserStatus.ONLINE);
////        User user2 = userService.create("달룡", UserStatus.ONLINE);
////        User user3 = userService.create("달례", UserStatus.ONLINE);
////        try {
////            userService.create("달선", UserStatus.ONLINE);
////        } catch (IllegalArgumentException e) {
////            System.out.println("중복 생성 방지 확인: " + e.getMessage());
////        }
////
////        System.out.println("\n=== 2. 채널 생성 및 입장 (저장 확인) ===");
////        Channel publicChannel = channelService.create("우리집", IsPrivate.PUBLIC, user2.getId());
////        channelService.joinChannel(user2.getId(), publicChannel.getId());
////        channelService.joinChannel(user3.getId(), publicChannel.getId());
////        Channel sisterChannel = channelService.create("언니집", IsPrivate.PUBLIC, user2.getId());
////        channelService.joinChannel(user2.getId(), sisterChannel.getId());
////        System.out.println("공용 채널 참여자 수: " + channelService.getChannelUsers(publicChannel.getId()).size() + "명");
////
////        System.out.println("\n=== 3. 메시지 전송 및 순서(Sorted) 확인 ===");
////        messageService.create(user1.getId(), publicChannel.getId(), "첫 번째 메시지");
////        messageService.create(user1.getId(), publicChannel.getId(), "배고픈 메시지");
////        messageService.create(user2.getId(), publicChannel.getId(), "두 번째 메시지");
////        messageService.create(user3.getId(), publicChannel.getId(), "세 번째 메시지");
////        messageService.create(user1.getId(), sisterChannel.getId(), "간식을 원하는 메시지");
////        System.out.println("[공용 채널 대화]");
////        printChannelMessage(publicChannel.getName(), channelService.getChannelMessages(publicChannel.getId()));
////
////
//////        System.out.println();
//////        System.out.println();
//////        printChannelMessage(publicChannel.getName(), channelService.getChannelMessages(publicChannel.getId()));
//////        System.out.println(channelService.getChannelUsers(publicChannel.getId()));
//////        System.out.println();
//////        System.out.println(user1.getName() + "삭제");
//////        userService.delete(user1.getId());
//////        printChannelMessage(publicChannel.getName(), channelService.getChannelMessages(publicChannel.getId()));
//////        System.out.println(channelService.getChannelUsers(publicChannel.getId()));
//////        System.out.println();
//////        System.out.println();
////
////        System.out.println("\n=== 4. DM(1:1) 생성 및 중복 방지 테스트 ===");
////        // 첫 번째 DM 전송 (채널 생성됨)
////        UUID dmId1 = messageService.sendDirectMessage(user1.getId(), user2.getId(), "달룡아 안녕?");
////        // 두 번째 DM 전송 (기존 채널을 찾아야 함)
////        messageService.sendDirectMessage(user2.getId(), user1.getId(), "응 달선아!");
////
////        System.out.println("달선님이 속한 채널 수: " + userService.getUserChannels(user1.getId()).size());
////
////        System.out.println("\n=== 5. 업데이트 및 파일 덮어쓰기 확인 ===");
////        System.out.println("수정 전: " + user1);
////        user1 = userService.update(user1.getId(), "대장달선", UserStatus.DONOTDISTURB);
////        // 다시 파일에서 읽어와서 확인
////        User checkUser = userService.findById(user1.getId());
////        System.out.println("수정 후(파일 재조회): " + checkUser);
////
////        System.out.println("\n=== 6. 검색 및 유저별 메시지 조회 ===");
////        String keyword = "배고파";
////        messageService.create(user1.getId(), publicChannel.getId(), "아 배고파!");
////        messageService.create(user1.getId(), sisterChannel.getId(), "물도 원한다 배고파");
////        printSearchedMessage(publicChannel.getName(), keyword, messageService.searchMessage(publicChannel.getId(), keyword));
////        printSearchedMessage(sisterChannel.getName(), keyword, messageService.searchMessage(sisterChannel.getId(), keyword));
////
////        System.out.println("\n=== 7. 삭제 테스트 (신중하게!) ===");
////        System.out.println("삭제 전 유저 수: " + userService.readAll().size());
////        userService.delete(user3.getId());
////        System.out.println("삭제 후 유저 수: " + userService.readAll().size());
////
////        // 주의: 삭제된 유저의 메시지를 조회하면 에러가 날 수 있으므로 체크 후 진행
////        System.out.println("삭제된 유저(user3)의 메시지함 조회 시도...");
////        try {
////            List<Message> user3Msgs = userService.getUserMessages(user3.getId());
////            System.out.println("메시지 개수: " + user3Msgs.size());
////        } catch (Exception e) {
////            System.out.println("삭제된 유저 조회 시 예외 처리 확인: " + e.getMessage());
////        }
////    }
////
////
////
////
////    private static void printUserMessage(String userName, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + userName + "님이 보낸 메시지 내역]");
////            messages.forEach(msg
////                    -> System.out.printf("- [%s] %s%n", msg.getChannel().getName(), msg.getContent()));
////        } else {
////            System.out.println(userName + "님이 보낸 메시지가 없습니다.");
////        }
////    }
////
////    private static void printUserChannel(String userName, List<Channel> channels) {
////        if (!channels.isEmpty()) {
////            System.out.println("[" + userName + "님의 채널들]");
////            channels.forEach(ch -> System.out.printf("- %s%n", ch.getName()));
////        } else {
////            System.out.println(userName + "님이 보유하신 채널이 없습니다.");
////        }
////    }
////
////    private static void printChannelMessage(String channelName, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + channelName + " 채널의 대화 내용]");
////            messages.forEach(msg -> System.out.printf("- [%s] %s%n", msg.getSender().getName(), msg.getContent()));
////        } else {
////            System.out.println(channelName + "채널에 대화 내용이 없습니다.");
////        }
////    }
////
////    private static void printSearchedMessage(String channelName, String searchContent, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + channelName + "] 채널에 '" + searchContent + "' 내용이 포함된 메시지]");
////            messages.forEach(msg -> System.out.printf("- [%s] : %s%n", msg.getSender().getName(), msg.getContent()));
////        } else {
////            System.out.println("[" + channelName + "] 채널에 '" + searchContent + "' 내용이 없습니다.");
////        }
////    }
////
////}
////
