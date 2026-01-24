package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserService implements UserService {

    public List<User> data = new ArrayList<>();
    ChannelService channelService;
    UserRepository userRepository;

    public JCFUserService(ChannelService channelService){
        this.channelService = channelService;
    }

    // User를 생성하고 해당 data에 삽입합니다.
    @Override
    public User createUser(String name, String email, String userId) {
        // 매개변수 유효성 체크
        Objects.requireNonNull(name, "유효하지 않은 이름입니다.");
        Objects.requireNonNull(email, "유효하지 않은 이메일입니다.");
        Objects.requireNonNull(userId, "유효하지 않은 ID입니다.");

        // 유저ID를 식별자로 삼아 중복 검증을 수행합니다.
        if(data.stream()
                .anyMatch(u -> userId.equals(u.getUserId()))){
            throw new IllegalStateException("해당 ID는 이미 존재합니다.");
        }

        // 유저 생성 및 data에 add.
        User user = new User(name, email, userId);
        data.add(user);
        return user;
    }


    // 유저ID를 식별자로 삼아 해당 유저를 읽어오고 유저를 리턴합니다.
    @Override
    public User readUser(String userId) {
        // 매개변수 유효성 검사
        Objects.requireNonNull(userId, "유효하지 않은 유저 ID입니다.");

        // data 리스트에서 매개변수로 들어온 유저ID를 가지는 유저 객체를 찾고 반환.
        // 없으면 예외를 던짐.
        return data.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));

    }

        // 채널의 UUID를 식별자로 삼아 해당 채널에 존재하는 유저 Set을 리턴합니다.
    public List<User> readUsersbyChannel(UUID channelID){
        // JCFUserService 생성 시 받은 클래스 매개변수 JCFChannelService의 메소드 사용.
        Channel channel = channelService.readChannel(channelID);
        return channel.getUsers();

    }



    // 유저ID를 식별자로 삼아 name, email을 수정합니다.
    @Override
    public User updateUser(String userId, String name, String email) {
        // 매개변수 유효성 검증
        Objects.requireNonNull(userId, "유효하지 않은 유저 아이디입니다.");

        // 찾고자 하는 유저를 유저ID를 통해 찾아냄.
        User user = readUser(userId);

        // name이 null이 아니라면 updateName을 호출
        // updateEmail이 null이 아니라면 updateEmail 호출
        Optional.ofNullable(name)
                .ifPresent(user::updateName);
        Optional.ofNullable(email)
                .ifPresent(user::updateEmail);

        return user;
    }

    
    // 유저ID를 식별자로 삼아 해당 유저를 data 리스트에서 삭제 및 타 도메인과 연결 끊기.
    @Override
    public void deleteUser(String userID) {
        // 매개변수 유효성 검증
        Objects.requireNonNull(userID, "유효하지 않은 식별자입니다.");

        // 유저 읽어오기
        User user = readUser(userID);

        // 유저가 속해있던 채널은 해당 유저를 kick.
        user.getChannelList().forEach(t-> t.kickUser(user));

        // 유저가 가지고 있던 channelList는 초기화.
        user.getChannelList().clear();

        // data에서 제거
        data.remove(user);

    }


    // 해당 data 리스트에 존재하는 모든 유저를 읽어옵니다.
    @Override
    public ArrayList<User> readAllUsers() {
        return (ArrayList<User>) data;
    }

    // 유저와 채널간의 관계를 설정합니다. (유저가 채널 참가)
    public void joinChannel(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        // 채널 서비스를 이용해 참가하고자 하는 채널 읽어오기.
        Channel channel = channelService.readChannel(channelID);

        // 유저 읽어오기
        User user = readUser(userID);

        // 읽어온 채널에 유저가 이미 가입되었다면 예외 던짐.
        if(channel.getUsers().contains(user)){
            throw new IllegalStateException("채널에 이미 가입되어 있습니다.");
        }

        // 해당 유저의 채널리스트에 해당 채널을 추가
        user.getChannelList().add(channel);

        // 채널 서비스를 이용하여 해당 채널의 유저리스트에 해당 유저를 추가
        channelService.userJoin(user.getUserId(), channel.getId());

        System.out.printf("%s 님이 %s 채널에 입장했습니다.%n", user.getUserId(), channel.getName());

    }

    // 유저와 채널의 관계를 끊어버립니다. (탈퇴)
    public void exitChannel(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        User user = readUser(userID);
        Channel channel = channelService.readChannel(channelID);

        user.getChannelList().remove(channel); // 인스턴스의 channelList 필드에서 매개변수로 입력받은 채널을 제거합니다.
        channel.getUsers().remove(user); // 입력받은 채널의 userList에서도 연결을 끊습니다.

        System.out.printf("%s 님이 %s 채널에서 퇴장했습니다.%n", user.getUserId(), channel.getName());
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void setChannelService(ChannelService channelService) {

    }


}