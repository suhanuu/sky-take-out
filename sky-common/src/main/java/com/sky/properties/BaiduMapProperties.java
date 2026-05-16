package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.baidu")
@Data
public class BaiduMapProperties {

    private String ak;// 百度地图ak
    private String shopAddress;// 商家地址
}