package org.example.backend.controller;

import org.example.backend.dto.ApiResponse;
import org.example.backend.dto.LoginResponse;
import org.example.backend.dto.MfaToggleRequest;
import org.example.backend.dto.MfaVerificationRequest;
import org.example.backend.dto.UserInfoDto;
import org.example.backend.entity.Token;
import org.example.backend.entity.User;
import org.example.backend.exception.MfaException;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.JwtService;
import org.example.backend.service.MfaService;
import org.example.backend.util.CustomUserDetails;
import org.example.backend.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {
    

    private final MfaService mfaService;
    

    private final UserRepository userRepository;
    

    private final JwtService jwtService;


    @Autowired
    public MfaController(MfaService mfaService, UserRepository userRepository, JwtService jwtService) {
        this.mfaService = mfaService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Object>> sendCode(@RequestParam String email) {
        mfaService.sendVerificationCode(email);
        return ResponseUtil.success(null, "Verification code sent to your email");
    }
    
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<ApiResponse<LoginResponse>> verifyCode(@RequestBody MfaVerificationRequest request) {
        if (!mfaService.verifyCode(request.getEmail(), request.getCode())) {
            throw new MfaException("Invalid or expired verification code");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
        jwtService.revokeAllUserTokens(user);
        
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        jwtService.saveToken(accessToken, user, Token.TokenType.ACCESS_TOKEN);
        jwtService.saveToken(refreshToken, user, Token.TokenType.REFRESH_TOKEN);
        
        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(UserInfoDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
        
        return ResponseUtil.success(response, "MFA verification successful");
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Object>> toggleMfa(@RequestParam String email, @RequestBody MfaToggleRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setMfaEnabled(request.isEnabled());
        userRepository.save(user);
        
        String message = request.isEnabled() ? "MFA enabled successfully" : "MFA disabled successfully";
        return ResponseUtil.success(null, message);
    }
}