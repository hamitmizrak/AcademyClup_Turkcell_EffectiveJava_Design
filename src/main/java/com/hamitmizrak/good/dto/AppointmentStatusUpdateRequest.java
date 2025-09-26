package com.hamitmizrak.good.dto;

/*
 CLEAN-UP ÖZETİ
 ✅ Net ve tek amaç: durum güncellemesi.
*/

import com.hamitmizrak.good.domain.AppointmentStatus;

public record AppointmentStatusUpdateRequest(
        long appointmentId,
        AppointmentStatus newStatus
) {}
