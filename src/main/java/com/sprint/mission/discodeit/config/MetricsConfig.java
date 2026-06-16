package com.sprint.mission.discodeit.config;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterBinder cacheHitRatioMetrics(CacheManager cacheManager) {
        return registry -> {
            // 1. 개별 캐시별 적중률 (cache.hit.ratio)
            cacheManager.getCacheNames().forEach(cacheName -> {
                Gauge.builder("cache.hit.ratio", () -> {
                    double hits = getCount(registry, cacheName, "hit");
                    double misses = getCount(registry, cacheName, "miss");
                    double total = hits + misses;
                    return total == 0 ? 0.0 : hits / total;
                })
                .tag("name", cacheName)
                .description("Cache hit ratio for " + cacheName)
                .register(registry);
            });

            // 2. 전체 캐시 통합 적중률 (cache.hit.ratio.global)
            Gauge.builder("cache.hit.ratio.global", () -> {
                double totalHits = registry.find("cache.gets").tag("result", "hit")
                        .functionCounters().stream()
                        .mapToDouble(FunctionCounter::count)
                        .sum();
                double totalMisses = registry.find("cache.gets").tag("result", "miss")
                        .functionCounters().stream()
                        .mapToDouble(FunctionCounter::count)
                        .sum();
                double total = totalHits + totalMisses;
                return total == 0 ? 0.0 : totalHits / total;
            })
            .description("Global cache hit ratio across all caches")
            .register(registry);
        };
    }

    private double getCount(MeterRegistry registry, String cacheName, String result) {
        FunctionCounter counter = registry.find("cache.gets")
                .tag("name", cacheName)
                .tag("result", result)
                .functionCounter();
        return (counter != null) ? counter.count() : 0.0;
    }
}
