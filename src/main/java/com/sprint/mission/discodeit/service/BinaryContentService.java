package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.BinaryContentPostDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
	@Qualifier("JCFBinaryContentRepository")
	private final BinaryContentRepository binaryContentRepository;

	private final BinaryContentMapper binaryContentMapper;

	public BinaryContent create(BinaryContentPostDTO binaryContentPostDTO) {
		return binaryContentRepository.save(binaryContentMapper.fromDto(binaryContentPostDTO));
	}

	public BinaryContent findById(UUID id) {
		return binaryContentRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 BinaryContent가 존재하지 않습니다."));
	}

	public List<BinaryContent> findAllByIdIn(List<UUID> idList) {
		return binaryContentRepository.findByIdIn(idList);
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
