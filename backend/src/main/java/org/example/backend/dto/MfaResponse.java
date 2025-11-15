package org.example.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MfaResponse {
    private boolean mfaRequired;
    private String message;
    private LoginResponse loginResponse;
}