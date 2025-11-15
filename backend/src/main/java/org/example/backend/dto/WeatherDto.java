package org.example.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherDto {
    private String name;
    private String description;
    private double temp;
}