package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.ReadStatusPatchDTO;
import com.sprint.mission.discodeit.dto.ReadStatusPostDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadStatusService {
	@Qualifier("JCFReadStatusRepository")
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;

	public ReadStatus create(ReadStatusPostDTO readStatusDTO) {
		userRepository.findById(readStatusDTO.userId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + readStatusDTO.userId() + "인 유저는 존재하지 않습니다.")
			);

		channelRepository.findById(readStatusDTO.channelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + readStatusDTO.channelId() + "인 채널은 존재하지 않습니다.")
			);

		// 이미 관련 객체가 존재하면 예외 발생
		readStatusRepository.findByUserIdAndChannelId(readStatusDTO.userId(), readStatusDTO.channelId())
			.ifPresent(rs -> {
				throw new RuntimeException("관련된 ReadStatus 객체가 이미 존재합니다.");
			});

		return readStatusRepository.save(
			new ReadStatus(
				readStatusDTO.userId(),
				readStatusDTO.channelId(),
				Instant.now()
			)
		);
	}

	public ReadStatus findById(UUID id) {
		return readStatusRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 ReadStatus는 존재하지 않습니다."));
	}

	public List<ReadStatus> findAllByUserId(UUID userId) {
		return readStatusRepository.findByUserId(userId);
	}

	public ReadStatus update(ReadStatusPatchDTO readStatusPatchDTO) {
		ReadStatus readStatus = readStatusRepository.findById(readStatusPatchDTO.readStatusId())
			.orElseThrow(() -> new NoSuchElementException(
				"id가 " + readStatusPatchDTO.readStatusId() + "인 ReadStatus는 존재하지 않습니다."));

		// 시간 정보만 최신으로 갱신 후 저장
		readStatus.updateLastReadTime();
		return readStatusRepository.save(readStatus);
	}

	public void delete(UUID id) {
		readStatusRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 ReadStatus는 존재하지 않습니다."));

		readStatusRepository.delete(id);
	}

}
