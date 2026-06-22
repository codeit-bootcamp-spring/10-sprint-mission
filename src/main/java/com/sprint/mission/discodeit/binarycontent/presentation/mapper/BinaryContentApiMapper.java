package com.sprint.mission.discodeit.binarycontent.presentation.mapper;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface BinaryContentApiMapper extends GenericApiMapper<BinaryContent, BinaryContentDto> {

  @Override
  BinaryContentDto toDto(BinaryContent entity);
}
