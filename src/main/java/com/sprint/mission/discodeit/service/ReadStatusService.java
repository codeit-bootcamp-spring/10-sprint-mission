package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadStatusService {
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;

	private final ReadStatusMapper readStatusMapper;

	public ReadStatusResponseDto create(ReadStatusPostDto readStatusPostDto) {
		userRepository.findById(readStatusPostDto.userId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + readStatusPostDto.userId() + "인 유저는 존재하지 않습니다.")
			);

		channelRepository.findById(readStatusPostDto.channelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + readStatusPostDto.channelId() + "인 채널은 존재하지 않습니다.")
			);

		// 이미 관련 객체가 존재하면 예외 발생
		readStatusRepository.findByUserIdAndChannelId(readStatusPostDto.userId(), readStatusPostDto.channelId())
			.ifPresent(rs -> {
				throw new RuntimeException("관련된 ReadStatus 객체가 이미 존재합니다.");
			});

		return readStatusMapper.toResponseDto(
			readStatusRepository.save(readStatusMapper.fromDto(readStatusPostDto))
		);
	}

	public ReadStatusResponseDto findById(UUID id) {
		return readStatusMapper.toResponseDto(readStatusRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 ReadStatus는 존재하지 않습니다."))
		);
	}

	public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
		return readStatusRepository.findByUserId(userId).stream()
			.map(readStatusMapper::toResponseDto)
			.collect(Collectors.toList());
	}

	public ReadStatusResponseDto update(UUID readStatusId) {
		ReadStatus readStatus = readStatusRepository.findById(readStatusId)
			.orElseThrow(() -> new NoSuchElementException(
				"id가 " + readStatusId + "인 ReadStatus는 존재하지 않습니다."));

		// 시간 정보만 최신으로 갱신 후 저장
		readStatus.updateLastReadTime();
		return readStatusMapper.toResponseDto(readStatusRepository.save(readStatus));
	}

	public void delete(UUID id) {
		readStatusRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 ReadStatus는 존재하지 않습니다."));

		readStatusRepository.delete(id);
	}

}
