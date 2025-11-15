package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WeatherResponse {
    private String name;
    private List<Weather> weather;
    private Main main;
    
    @Getter
    @Setter
    public static class Weather {
        private String description;
    }
    
    @Getter
    @Setter
    public static class Main {
        private double temp;
    }
}