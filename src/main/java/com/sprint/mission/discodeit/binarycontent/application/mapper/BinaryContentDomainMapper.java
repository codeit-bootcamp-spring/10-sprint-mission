package com.sprint.mission.discodeit.binarycontent.application.mapper;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface BinaryContentDomainMapper {

  List<BinaryContent> toEntityFrom(List<FileUploadRequest> files);

  BinaryContent toEntityFrom(FileUploadRequest dto);
}
