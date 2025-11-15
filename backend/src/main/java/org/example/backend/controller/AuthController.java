package org.example.backend.controller;

import org.example.backend.dto.ApiResponse;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;
import org.example.backend.dto.MfaResponse;
import org.example.backend.dto.RefreshTokenRequest;
import org.example.backend.dto.UserInfoDto;
import org.example.backend.entity.Token;
import org.example.backend.entity.User;
import org.example.backend.service.AuthenticationService;
import org.example.backend.service.JwtService;
import org.example.backend.service.MfaService;
import org.example.backend.service.TokenService;
import org.example.backend.util.CustomUserDetails;
import org.example.backend.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final MfaService mfaService;

    @Autowired
    public AuthController(AuthenticationService authenticationService, JwtService jwtService, MfaService mfaService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.mfaService = mfaService;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<ApiResponse<MfaResponse>> login(@RequestBody LoginRequest loginRequest) {
        CustomUserDetails authenticatedUser = authenticationService.authenticate(loginRequest);
        User user = authenticatedUser.getUser();
        
        if (user.isMfaEnabled()) {
            mfaService.sendVerificationCode(user.getEmail());
            MfaResponse mfaResponse = MfaResponse.builder()
                    .mfaRequired(true)
                    .message("MFA verification required. Check your email for verification code.")
                    .build();
            return ResponseUtil.success(mfaResponse, "MFA verification required");
        }
        
        jwtService.revokeAllUserTokens(user);
        
        String accessToken = jwtService.generateAccessToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        
        jwtService.saveToken(accessToken, user, Token.TokenType.ACCESS_TOKEN);
        jwtService.saveToken(refreshToken, user, Token.TokenType.REFRESH_TOKEN);
        
        LoginResponse loginResponse = LoginResponse.builder()
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
        
        MfaResponse mfaResponse = MfaResponse.builder()
                .mfaRequired(false)
                .message("Login successful")
                .loginResponse(loginResponse)
                .build();
        
        return ResponseUtil.success(mfaResponse, "Login successful");
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenService tokenService = new TokenService(null, null, jwtService);
        LoginResponse response = tokenService.refreshToken(request.getRefreshToken());
        return ResponseUtil.success(response, "Token refreshed successfully");
    }
    
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        TokenService tokenService = new TokenService(null, null, jwtService);
        tokenService.logout(token);
        return ResponseUtil.success(null, "Logout successful");
    }
}