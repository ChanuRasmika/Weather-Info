package org.example.backend.service;

import org.example.backend.entity.VerificationCode;
import org.example.backend.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class MfaService {
    

    private final VerificationCodeRepository verificationCodeRepository;
    

    private final EmailService emailService;
    
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public MfaService(VerificationCodeRepository verificationCodeRepository, EmailService emailService) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void sendVerificationCode(String email) {
        verificationCodeRepository.deleteByEmail(email);
        
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        
        VerificationCode verificationCode = new VerificationCode(email, code, expiresAt);
        verificationCodeRepository.save(verificationCode);
        
        emailService.sendVerificationCode(email, code);
    }
    
    public boolean verifyCode(String email, String code) {
        return verificationCodeRepository.findByEmailAndCodeAndUsedFalse(email, code)
                .map(verificationCode -> {
                    if (verificationCode.getExpiresAt().isAfter(LocalDateTime.now())) {
                        verificationCode.setUsed(true);
                        verificationCodeRepository.save(verificationCode);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
    
    private String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
}