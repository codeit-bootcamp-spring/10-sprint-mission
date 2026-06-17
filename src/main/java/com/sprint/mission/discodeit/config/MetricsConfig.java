package com.sprint.mission.discodeit.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  /*
  TimedAspect : 실제로 시간을 측정하는 AOP
  MeterRegistry : 메트릭 저장소
  Actuator / Prometheus : 외부에서 조회

  TimedAspect를 MeterRegistry와 함께 빈으로 등록해서, @Timed 어노테이션이 실제로 동작하도록 AOP를 활성화하는 설정
   */
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry); // @Timed 동작을 위해서 필요함
  }

}
