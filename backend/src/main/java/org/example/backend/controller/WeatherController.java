package org.example.backend.controller;

import org.example.backend.dto.ApiResponse;
import org.example.backend.dto.CityDto;
import org.example.backend.dto.WeatherDto;
import org.example.backend.service.WeatherService;
import org.example.backend.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<CityDto>>> getCities() {
        try {
            List<CityDto> cities = weatherService.getCities();
            return ResponseUtil.success(cities, "Cities retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to load cities: " + e.getMessage(), 500);
        }
    }
    
    @GetMapping("/{cityCode}")
    public ResponseEntity<ApiResponse<WeatherDto>> getWeather(@PathVariable String cityCode) {
        try {
            if (cityCode == null || cityCode.equals("undefined") || cityCode.trim().isEmpty()) {
                return ResponseUtil.error("Invalid city code provided", 400);
            }
            WeatherDto weather = weatherService.getWeatherByCityCode(cityCode);
            return ResponseUtil.success(weather, "Weather data retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to fetch weather data: " + e.getMessage(), 500);
        }
    }
}