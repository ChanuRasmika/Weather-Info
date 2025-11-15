package org.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityDto {
    @JsonProperty("CityCode")
    private String cityCode;
    
    @JsonProperty("CityName")
    private String cityName;
    
    @JsonProperty("Temp")
    private String temp;
    
    @JsonProperty("Status")
    private String status;
}