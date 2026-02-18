package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
	private static UserService instance;
	// JCF를 활용하여 데이터를 저장하는 필드
	private final List<User> data;

	// data 필드를 생성자로 초기화
	private JCFUserService() {
		data = new ArrayList<User>();
	}

	// 싱글톤 패턴?
	public static UserService getInstance() {
		if (instance == null)
			instance = new JCFUserService();
		return instance;
	}

	//  [ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
	@Override
	public UserResponseDto create(UserPostDto userPostDTO, MultipartFile profileImage) {
		//        User newUser = new User(nickName, userName, email, phoneNumber);
		//        data.add(newUser);
		//        return newUser;
		return null;
	}

	@Override
	public UserResponseDto findById(UUID userId) {
		//        return data.stream()
		//            .filter(user -> user.getId().equals(id))
		//            .findFirst()
		//            .orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 유저를 찾을 수 없습니다."));
		return null;
	}

	@Override
	public UserResponseDto findByUserName(String userName) {
		//        return data.stream()
		//            .filter(user -> user.getUserName().equals(userName))
		//            .findFirst()
		//            .orElseThrow(() -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다."));
		return null;
	}

	@Override
	public List<UserResponseDto> findAll() {
		//        return data;
		return null;
	}

	@Override
	public UserResponseDto updateUser(UUID userId, UserPatchDto userPatchDTO) {
		//        User updatedUser = data.stream()
		//            .filter(u -> u.getId().equals(authorId))
		//            .findFirst()
		//            .orElseThrow(() -> new NoSuchElementException("id가 " + authorId + "인 유저를 찾을 수 없습니다."));
		//
		//        // input이 null이 아닌 필드 업데이트
		//        Optional.ofNullable(user.getNickName())
		//            .ifPresent(updatedUser::updateNickName);
		//        Optional.ofNullable(user.getUserName())
		//            .ifPresent(updatedUser::updateUserName);
		//        Optional.ofNullable(user.getEmail())
		//            .ifPresent(updatedUser::updateEmail);
		//        Optional.ofNullable(user.getPhoneNumber())
		//            .ifPresent(updatedUser::updatePhoneNumber);
		//
		//        return updatedUser;
		return null;
	}

	@Override
	public void delete(UUID id) {
		if (!data.removeIf(user -> user.getId().equals(id)))
			throw new NoSuchElementException("id가 " + id + "인 유저는 존재하지 않습니다.");
	}
}
