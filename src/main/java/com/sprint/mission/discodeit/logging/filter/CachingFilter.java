package com.sprint.mission.discodeit.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // MdcFilter 다음에 실행
public class CachingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.contains("/download") || path.startsWith("/api/files");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest requestToCache = request;
        boolean isMultipart = request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");

        if (!isMultipart) { // 멀티파트 요청이 아닌 경우만 캐싱
            requestToCache = new ContentCachingRequestWrapper(request);
        }

        ContentCachingResponseWrapper responseToCache = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestToCache, responseToCache);
        } finally {
            responseToCache.copyBodyToResponse();
        }
    }
}