package com.sprint.mission.discodeit.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;

	private final BinaryContentMapper binaryContentMapper;

	public BinaryContent create(BinaryContentPostDto binaryContentPostDto) {
		return binaryContentRepository.save(binaryContentMapper.fromDto(binaryContentPostDto));
	}

	public BinaryContentResponseDto findById(UUID id) throws IOException {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + id + "인 BinaryContent가 존재하지 않습니다.")
			);

		File file = new File(
			Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "images",
				binaryContent.getFileName()).toString());

		byte[] fileBytes = Files.readAllBytes(file.toPath());
		String base64 = Base64.getEncoder().encodeToString(fileBytes);

		return new BinaryContentResponseDto(
			"image/" + StringUtils.getFilenameExtension(binaryContent.getFileName()),
			base64
		);
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
