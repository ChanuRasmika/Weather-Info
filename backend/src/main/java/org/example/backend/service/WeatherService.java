package org.example.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.CitiesWrapper;
import org.example.backend.dto.CityDto;
import org.example.backend.dto.WeatherDto;
import org.example.backend.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class WeatherService {
    

    private final RestTemplate restTemplate;
    
    @Value("${openweather.api.key}")
    private String apiKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CityDto> getCities() throws IOException {
        ClassPathResource resource = new ClassPathResource("cities.json");
        CitiesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), CitiesWrapper.class);
        List<CityDto> cities = wrapper.getList();
        System.out.println("Loaded cities: " + cities.size());
        if (!cities.isEmpty()) {
            System.out.println("First city: " + cities.get(0).getCityCode() + " - " + cities.get(0).getCityName());
        }
        return cities;
    }
    
    public WeatherDto getWeatherByCityCode(String cityCode) {
        String url = "https://api.openweathermap.org/data/2.5/weather?id=" + cityCode + "&appid=" + apiKey + "&units=metric";
        
        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
        
        return WeatherDto.builder()
                .name(response.getName())
                .description(response.getWeather().get(0).getDescription())
                .temp(response.getMain().getTemp())
                .build();
    }
}