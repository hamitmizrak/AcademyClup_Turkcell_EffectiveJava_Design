package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ Basit kimlik doğrulama için minimal record.
*/

public record LoginRequest(
        String username,
        String password
) {}
