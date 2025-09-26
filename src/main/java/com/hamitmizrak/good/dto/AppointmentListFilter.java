package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ Opsiyonel filtreler için null taşımaya uygun record (UI/CLI boş geçilebilir).
*/

import com.hamitmizrak.good.domain.AppointmentStatus;

public record AppointmentListFilter(
        Long doctorId,
        Long patientId,
        AppointmentStatus status
) {}
