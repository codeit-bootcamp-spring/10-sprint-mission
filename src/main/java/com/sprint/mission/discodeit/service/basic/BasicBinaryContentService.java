package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    @Value("${discodeit.upload.attachment}")
    private String ATTACHMENT_DIR;

    @Override
    public BinaryContentDto.response create(BinaryContentDto.createRequest createReq) {
        BinaryContent binaryContent = new BinaryContent(createReq.contentType(), createReq.filename(), ATTACHMENT_DIR);
        // 첨부파일 저장
        Path attachmentDir = Paths.get(ATTACHMENT_DIR);
        String fileName = binaryContent.getId() + "." + StringUtils.getFilenameExtension(createReq.filename());
        try {
            Files.createDirectories(attachmentDir);
            Files.write(attachmentDir.resolve(fileName), createReq.bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binaryContentRepository.save(binaryContent);
        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentDto.response findById(UUID uuid) {
        return binaryContentRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 binaryContent입니다"));
    }

    @Override
    public List<BinaryContentDto.response> findAllByIdIn(List<UUID> uuids) {
        return binaryContentRepository.findAllByIdIn(uuids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        binaryContentRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 binaryContent입니다"));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ATTACHMENT_DIR), uuid + ".*")) {
            for (Path p : stream) {
                Files.deleteIfExists(p);
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binaryContentRepository.deleteById(uuid);
    }

    private BinaryContentDto.response toResponse(BinaryContent binaryContent) {
        byte[] bytes = null;
        String fileName = binaryContent.getId() + "." + StringUtils.getFilenameExtension(binaryContent.getFileName());
        try {
            bytes = Files.readAllBytes(Paths.get(binaryContent.getUrl(), fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BinaryContentDto.response(binaryContent.getId(), binaryContent.getCreatedAt(),
                binaryContent.getContentType().getMimeType(),
                binaryContent.getFileName(), bytes);
    }
}
