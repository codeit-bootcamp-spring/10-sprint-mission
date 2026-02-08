package com.sprint.mission.discodeit.testMethod;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCrud {

    private final UserService userService;
    private final ChannelService channerService;

    // 유저 회원가입
    public void SignUp(String name, String email, String password, String profileImagePath) {
        userService.create(new UserDto.UserRequest(name, email, password), profileImagePath);
        System.out.println("회원가입에 성공하였습니다. \n이름: " + name + "\n이메일: " + email);
    }

    // 유저 회원탈퇴
    public void SingOut(String name, String password) {
        User member = userService.CheckUser(name, password);
        System.out.println("회원탈퇴에 성공하였습니다. \n이름: " + name + "\n이메일: " + member.getEmail());
    }

    // 유저 정보 출력
    public void UserInfo(String name, String password) {
        User member = userService.CheckUser(name, password);
        System.out.println(member);
    }

    // 유저 정보 수정
    public void UpdateUser(String originName, String originPassword, String newName, String newEmail, String newPassword, String newFilePath) {
        User member = userService.CheckUser(originName, originPassword);
        userService.update(member.getId(), new UserDto.UserRequest(newName, newEmail, newPassword), newFilePath);
        System.out.println(member);
    }

    // 회원 목록 출력
    public void ListUsers() {
        System.out.println("현재 유저 목록: ");
        userService.findAll().forEach(user -> System.out.println(user.name()));
    }

    // 유저 상태 출력
    public void CheckUserStatus(String userName, String password) {
        User member = userService.CheckUser(userName, password);
        boolean status = userService.findById(member.getId()).isOnline();
        System.out.println("현재 " + userName + " 유저는 " + ((status) ? "온라인" : "오프라인") + " 상태입니다.");
    }

    // 유저 채널 가입
    public void JoinChannel(String userName, String password, String channelName) {
        User member = userService.CheckUser(userName, password);
        Channel channel = channerService.CheckChannel(channelName);
        userService.joinChannel(member.getId(), channel.getId());
        System.out.println(userName + "유저가 " + channelName +" 채널에 가입하였습니다.");
    }

    // 유저 채널 탈퇴
    public void LeaveChannel(String userName, String password, String channelName) {
        User member = userService.CheckUser(userName, password);
        Channel channel = channerService.CheckChannel(channelName);
        userService.leaveChannel(member.getId(), channel.getId());
        System.out.println(userName + "유저가 " + channelName +" 채널에서 탈퇴하였습니다.");
    }

}
