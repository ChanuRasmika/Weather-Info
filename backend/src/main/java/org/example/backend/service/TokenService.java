package org.example.backend.service;

import org.example.backend.dto.LoginResponse;
import org.example.backend.dto.UserInfoDto;
import org.example.backend.entity.Token;
import org.example.backend.entity.User;
import org.example.backend.repository.TokenRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.util.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {
    
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        var tokenEntity = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        
        if (tokenEntity.isExpired() || tokenEntity.isRevoked()) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }
        
        User user = tokenEntity.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
        jwtService.revokeAllUserTokens(user);
        
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        
        jwtService.saveToken(newAccessToken, user, Token.TokenType.ACCESS_TOKEN);
        jwtService.saveToken(newRefreshToken, user, Token.TokenType.REFRESH_TOKEN);
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(UserInfoDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }

    @Transactional
    public void logout(String token) {
        var tokenEntity = tokenRepository.findByToken(token);
        if (tokenEntity.isPresent()) {
            User user = tokenEntity.get().getUser();
            jwtService.revokeAllUserTokens(user);
        }
    }
}