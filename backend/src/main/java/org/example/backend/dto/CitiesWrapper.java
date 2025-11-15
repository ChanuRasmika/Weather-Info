package org.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CitiesWrapper {
    @JsonProperty("List")
    private List<CityDto> list;
}