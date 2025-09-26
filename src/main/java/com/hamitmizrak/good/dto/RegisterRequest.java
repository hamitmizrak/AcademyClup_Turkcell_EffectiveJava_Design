package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ Kayıt için gerekli minimal alan seti.
*/

public record RegisterRequest(
        String username,
        String email,
        String password
) {}
