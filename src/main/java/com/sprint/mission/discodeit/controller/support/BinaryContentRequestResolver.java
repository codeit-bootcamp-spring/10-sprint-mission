package com.sprint.mission.discodeit.controller.support;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BinaryContentRequestResolver {

    public Optional<BinaryContentCreateRequest> resolveOptional(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    )
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file");
        }
    }

    public List<BinaryContentCreateRequest> resolveList(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Failed to read file");
                    }
                })
                .toList();
    }
}