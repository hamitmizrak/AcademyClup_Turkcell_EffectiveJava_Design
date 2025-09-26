package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ "me" çıktısı için sade, taşıması kolay record.
*/

public record WhoAmIResponse(
        long id,
        String username,
        String role
) {}
