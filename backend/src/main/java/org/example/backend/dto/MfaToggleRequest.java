package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfaToggleRequest {
    private boolean enabled;
}