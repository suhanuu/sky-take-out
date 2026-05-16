package com.sky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class BaiduMapUtil {

    private String ak;
    private String shopAddress;
    private  RestTemplate restTemplate;

    private static final String GEOCODING_URL = "http://api.map.baidu.com/geocoding/v3/";
    private static final String DIRECTION_LITE_URL = "http://api.map.baidu.com/directionlite/v1/driving";
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public boolean validateDeliveryRange(String userAddress) {
        try {
            // 商家地址解析
            double[] shopCoords = getCoordinates(shopAddress);
            if (shopCoords == null) {
                log.error("无法解析商家门店地址: {}", shopAddress);
                // 【修改】不要 throw，而是返回 false
                return false;
            }

            // 用户地址解析
            double[] userCoords = getCoordinates(userAddress);
            if (userCoords == null) {
                log.error("无法解析用户收货地址: {}", userAddress);
                return false;
            }

            Double distance = calculateDistance(
                    shopCoords[1], shopCoords[0],
                    userCoords[1], userCoords[0]
            );

            if (distance == null) {
                log.error("无法计算配送距离");
                return false;
            }

            log.info("配送距离: {} 米", distance);
            return distance <= 5000;

        } catch (Exception e) {
            log.error("配送范围校验失败", e);
            return false;
        }
    }

    private double[] getCoordinates(String address) {
        if (address == null) {
            return null;
        }
        // --- 【关键修复步骤 2：强制截断长度】 ---
        // 百度API对地址长度有限制（通常建议控制在60-80字符以内）
        int maxLength = 80;
        if (address.length() > maxLength) {
            log.warn("地址长度超过限制 ({} > {})，已自动截断: {}", address.length(), maxLength, address);
            address = address.substring(0, maxLength);
        }

        // --- 【关键修复步骤 3：打印清洗后的地址用于调试】 ---
        log.info("【清洗后】准备解析的地址: {}", address);

        // 2. 构建请求URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                .queryParam("address", address)
                .queryParam("output", "json")
                .queryParam("ak", ak);

        // 【关键修改】增加城市限定，防止跨城搜索失败
        // 如果你的商家固定在北京，可以强制指定城市
        if (address.contains("北京")) {
            builder.queryParam("city", "北京市");
        }

        try {
            String url = builder.toUriString();
            log.info("调用百度地图地理编码API，地址: {}", address);
            log.info("完整请求URL: {}", url); // 复制这个URL去浏览器直接访问，看返回什么

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            log.info("百度地图API原始响应: {}", responseBody); // 重点查看这里的JSON

            JsonNode rootNode = objectMapper.readTree(responseBody);
            int status = rootNode.get("status").asInt();

            if (status == 0) {
                JsonNode locationNode = rootNode.get("result").get("location");
                double lng = locationNode.get("lng").asDouble();
                double lat = locationNode.get("lat").asDouble();
                log.info("地理编码成功 - 经度: {}, 纬度: {}", lng, lat);
                return new double[]{lng, lat};
            } else {
                // 【关键修改】打印具体的错误码和消息
                String message = rootNode.has("message") ? rootNode.get("message").asText() : "未知错误";
                log.error("地理编码失败 - status: {}, message: {}", status, message);
                // 返回 null，但不要立即抛出 RuntimeException，让 validateDeliveryRange 处理
            }
        } catch (Exception e) {
            log.error("地理编码异常，地址: {}", address, e);
        }
        return null;
    }




    private Double calculateDistance(double originLng, double originLat, double destLng, double destLat) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DIRECTION_LITE_URL)
                .queryParam("origin", originLat + "," + originLng)
                .queryParam("destination", destLat + "," + destLng)
                .queryParam("ak", ak);

        try {
            String url = builder.toUriString();
            log.info("调用百度地图距离计算API，URL: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            log.info("百度地图距离API原始响应: {}", responseBody);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String status = rootNode.get("status").asText();

            if ("0".equals(status)) {
                JsonNode routesNode = rootNode.get("result").get("routes");
                if (routesNode != null && !routesNode.isEmpty()) {
                    int distance = routesNode.get(0).get("distance").asInt();
                    log.info("距离计算成功: {} 米", distance);
                    return (double) distance;
                }
            } else {
                String message = rootNode.has("message") ? rootNode.get("message").asText() : "未知错误";
                log.warn("距离计算接口错误 - status: {}, message: {}", status, message);
            }
        } catch (Exception e) {
            log.error("距离计算异常", e);
        }
        return null;
    }



//    @Data
//    static class GeocodeResponse {
//        private int status;
//        private String message;
//        private Result result;
//
//        @Data
//        static class Result {
//            private Location location;
//
//            @Data
//            static class Location {
//                private double lng;
//                private double lat;
//            }
//        }
//    }
//
//    @Data
//    static class DirectionResponse {
//        private String status;
//        private String message;
//        private Result result;
//
//        @Data
//        static class Result {
//            private List<Route> routes;
//        }
//
//        @Data
//        static class Route {
//            private int distance;
//            private int duration;
//        }
//    }
}
