package com.sprint.mission.discodeit.global.web.config;

import com.sprint.mission.discodeit.global.web.filter.MdcLoggingFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class MdcLoggingFilterConfig {

  @Bean
  public FilterRegistrationBean<MdcLoggingFilter> loggingFilter() {
    FilterRegistrationBean<MdcLoggingFilter> loggingFilter = new FilterRegistrationBean<>();
    loggingFilter.setFilter(new MdcLoggingFilter());
    loggingFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
    loggingFilter.addUrlPatterns("/*");
    loggingFilter.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
    return loggingFilter;
  }
}
