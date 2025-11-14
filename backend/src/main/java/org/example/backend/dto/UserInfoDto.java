package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {
    private Integer id;
    private String email;
    private String role;
}