package org.example.backend.repository;

import org.example.backend.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokenByUser(Integer userId);
    
    Optional<Token> findByToken(String token);
    
    void deleteByUserIdAndTokenType(Integer userId, Token.TokenType tokenType);
}