package com.sprint.mission.discodeit.dto.message;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record MessageRequestCreateDto(String content,
                                      UUID channelId,
                                      UUID authorId,
                                      List<MultipartFile> attachments) {
}
