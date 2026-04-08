package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.inspector.QueryInspector;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@RequiredArgsConstructor
public class HibernateConfig {

  private final QueryInspector queryInspector;

  @Bean
  public HibernatePropertiesCustomizer config() {
    return properties -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, queryInspector);
  }
}
