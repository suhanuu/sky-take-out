package com.sky.config;

import com.sky.properties.BaiduMapProperties;
import com.sky.utils.BaiduMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class BaiduMapConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        log.info("创建RestTemplate对象...");

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            );
            return execution.execute(request, body);
        });

        restTemplate.getMessageConverters()
                .add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public BaiduMapUtil baiduMapUtil(BaiduMapProperties baiduMapProperties, RestTemplate restTemplate) {
        log.info("创建BaiduMapUtil对象，商家地址: {}", baiduMapProperties.getShopAddress());
        return new BaiduMapUtil(
                baiduMapProperties.getAk(),
                baiduMapProperties.getShopAddress(),
                restTemplate
        );
    }
}
