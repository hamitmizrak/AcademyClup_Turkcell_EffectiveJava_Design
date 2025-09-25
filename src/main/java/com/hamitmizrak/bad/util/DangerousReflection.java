package com.hamitmizrak.bad.util;

import com.hamitmizrak.bad.repository.AppointmentRepository;
import java.lang.reflect.Field;

/*
❌ KÖTÜ: Bu modülün özeti – neden kötü?
İş kuralları servis yerine CLI’da.
Global paylaşımlar, senkron yok, Reflection ile konfig kurcalama.
Hata/validasyon/i18n yok, log yok, güvenlik yok.
*/

public class DangerousReflection {
    public static void mutateDbUrl() {
        try {
            Field f = AppointmentRepository.class.getDeclaredField("URL");
            f.setAccessible(true); // Kötü: güvenlik açıkları
            f.set(null, "jdbc:h2:~/hospital_BAD_SURPRISE;AUTO_SERVER=TRUE");
            System.out.println("DB URL değiştirildi (KÖTÜ UYGULAMA).");
        } catch (Exception e) {
            // Kötü: sessiz
        }
    }
}
