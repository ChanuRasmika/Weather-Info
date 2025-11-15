package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfaVerificationRequest {
    private String email;
    private String code;
}