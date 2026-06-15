package com.sprint.mission.discodeit.binarycontent.presentation.mapper;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.mapper.GenericMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface BinaryContentMapper extends GenericMapper<BinaryContent, BinaryContentDto> {

  List<BinaryContent> toEntityFrom(List<FileUploadRequest> files);

  BinaryContent toEntityFrom(FileUploadRequest dto);

}
