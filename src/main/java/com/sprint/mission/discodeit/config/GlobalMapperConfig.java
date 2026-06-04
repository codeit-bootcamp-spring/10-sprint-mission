package com.sprint.mission.discodeit.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring", // spring 빈으로 등록
    unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않은 필드가 있어도 경고를 무시하도록 설정
)
public interface GlobalMapperConfig {

}
