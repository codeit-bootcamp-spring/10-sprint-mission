package com.sprint.mission.discodeit.global.web.filter;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.global.web.mvc.filter.MdcLoggingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class MdcLoggingFilterTest {

  @Test
  @DisplayName("doFilterInternal - MDC에 traceId를 추가하고 체인 실행 후 지운다.")
  void doFilterInternal() throws ServletException, IOException {
    MdcLoggingFilter filter = new MdcLoggingFilter();
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    
    // Custom filter chain to verify MDC during the filter chain execution
    FilterChain filterChain = (req, res) -> {
      String traceId = MDC.get("traceId");
      assertThat(traceId).isNotNull();
      assertThat(traceId).isNotBlank();
    };

    filter.doFilter(request, response, filterChain);

    // Verify MDC is cleared after execution
    assertThat(MDC.get("traceId")).isNull();
  }
}
