package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface BinaryContentMapper extends BaseMapper<BinaryContent, BinaryContentDto> {

  default Optional<BinaryContent> toEntityFrom(Optional<FileUploadDto> file) {
    return file.map(this::toEntityFrom);
  }

  List<BinaryContent> toEntityFrom(List<FileUploadDto> files);

  BinaryContent toEntityFrom(FileUploadDto dto);

}
