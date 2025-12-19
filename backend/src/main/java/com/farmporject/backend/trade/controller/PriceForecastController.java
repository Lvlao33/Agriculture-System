package com.farmporject.backend.trade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

/**
 * 价格预测控制器
 * 代理FastAPI预测服务（运行在8000端口）
 */
@RestController
@RequestMapping("/price")
public class PriceForecastController {

    private static final String FORECAST_SERVICE_URL = "http://localhost:8000/price/forecast";
    private final RestTemplate restTemplate;

    public PriceForecastController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取价格预测
     * GET /price/forecast?commodity=苹果&horizon=7
     */
    @GetMapping("/forecast")
    public ResponseEntity<?> getPriceForecast(
            @RequestParam("commodity") String commodity,
            @RequestParam(value = "horizon", defaultValue = "7") Integer horizon) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 构建请求URL
            String url = FORECAST_SERVICE_URL + "?commodity=" + commodity + "&horizon=" + horizon;
            
            // 调用FastAPI服务
            @SuppressWarnings("unchecked")
            Map<String, Object> forecastData = restTemplate.getForObject(url, Map.class);
            
            if (forecastData != null) {
                response.put("flag", true);
                response.put("data", forecastData);
                return ResponseEntity.ok(response);
            } else {
                response.put("flag", false);
                response.put("message", "预测服务返回空数据");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (HttpClientErrorException.NotFound e) {
            response.put("flag", false);
            response.put("message", "该品类暂无预测模型，请先训练模型");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (HttpClientErrorException.BadRequest e) {
            response.put("flag", false);
            response.put("message", "请求参数错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (ResourceAccessException e) {
            response.put("flag", false);
            response.put("message", "无法连接到预测服务，请确保FastAPI服务已启动（端口8000）");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "预测服务异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 健康检查
     * GET /price/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        try {
            String url = "http://localhost:8000/health";
            @SuppressWarnings("unchecked")
            Map<String, Object> health = restTemplate.getForObject(url, Map.class);
            response.put("flag", true);
            response.put("forecastService", health);
            response.put("message", "预测服务运行正常");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "预测服务不可用: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}

