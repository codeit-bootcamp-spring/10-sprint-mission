package com.sprint.mission.discodeit.service.file;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.sprint.mission.discodeit.dto.UserPatchDTO;
import com.sprint.mission.discodeit.dto.UserPostDTO;
import com.sprint.mission.discodeit.dto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIo;

public class FileUserService implements UserService {
	private static UserService instance;
	private final FileIo<User> fileIo;

	private FileUserService(@Value("${discodeit.repository.file-directory}") String directory) {
		this.fileIo = new FileIo<>(Paths.get(directory).resolve(User.class.getSimpleName().toLowerCase()));
		this.fileIo.init();
	}

	public static UserService getInstance() {
		if (instance == null)
			instance = new FileUserService("");
		return instance;
	}

	@Override
	public User create(UserPostDTO userPostDTO) {
		//        User newUser = new User(nickName, userName, email, phoneNumber);
		//
		//        List<User> users = fileIo.load();
		//        users.add(newUser);
		//        fileIo.save(newUser.getId(), newUser);
		//
		//        return newUser;
		return null;
	}

	@Override
	public UserResponseDTO findById(UUID userId) {
		//        return fileIo.load().stream()
		//            .filter(u -> u.getId().equals(id))
		//            .findFirst()
		//            .orElseThrow(
		//                () -> new NoSuchElementException("id가 " + id + "인 유저를 찾을 수 없습니다.")
		//            );
		return null;
	}

	@Override
	public UserResponseDTO findByUserName(String userName) {
		//        return fileIo.load().stream()
		//            .filter(u -> u.getUserName().equals(userName))
		//            .findFirst()
		//            .orElseThrow(
		//                () -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다.")
		//            );
		return null;
	}

	@Override
	public List<UserResponseDTO> findAll() {
		//        return fileIo.load();
		return null;
	}

	@Override
	public User updateUser(UserPatchDTO userPatchDTO) {
		//        User updatedUser = this.findById(userId);
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
		//        fileIo.save(userId, updatedUser);
		//
		//        return updatedUser;

		return null;
	}

	@Override
	public void delete(UUID userId) {
		try {
			fileIo.delete(userId);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
