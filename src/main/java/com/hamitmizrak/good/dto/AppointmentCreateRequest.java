package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ record kullanımı: immutability + auto equals/hash/toString.
 ✅ Zorunlu alanlar net, anlamlı isimlendirme.
*/

import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        long patientId,
        long doctorId,
        LocalDateTime dateTime,
        String note
) {}
