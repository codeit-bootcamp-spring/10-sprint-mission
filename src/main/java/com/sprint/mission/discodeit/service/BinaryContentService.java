package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.BinaryContentPostDTO;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;

	private final BinaryContentMapper binaryContentMapper;

	public BinaryContentResponseDTO create(BinaryContentPostDTO binaryContentPostDTO) {
		return binaryContentMapper.toResponseDto(
			binaryContentRepository.save(binaryContentMapper.fromDto(binaryContentPostDTO))
		);
	}

	public BinaryContentResponseDTO findById(UUID id) {
		return binaryContentMapper.toResponseDto(binaryContentRepository.findById(id)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + id + "인 BinaryContent가 존재하지 않습니다.")
			)
		);
	}

	public List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> idList) {
		return binaryContentRepository.findByIdIn(idList).stream()
			.map(binaryContentMapper::toResponseDto)
			.collect(Collectors.toList());
	}

	public void delete(UUID id) {
		binaryContentRepository.findById(id)
			.ifPresentOrElse(
				value -> binaryContentRepository.delete(id),
				() -> {
					throw new NoSuchElementException("id가 " + id + "인 BinaryContent가 존재하지 않습니다.");
				}
			);
	}
}
