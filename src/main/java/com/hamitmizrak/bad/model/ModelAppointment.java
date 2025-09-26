package com.hamitmizrak.bad.model;


import com.hamitmizrak.bad.common.CommonAudited;

import java.util.*;

/*
 ❌ KÖTÜ:Appointment (God-object’e giden yol, public alanlar, inner class, raw types)
Ne işe yarıyor? Randevu temel modeli.
Neden kötü? public alanlar; Date kullanımı; iç sınıfla kötü comparator; raw Comparable; text blocks içinde SQL saklama (modelde SQL?!), pattern matching yanlış yerde.
 */

/*
 ❌ KÖTÜ:
Bu modülün özeti – neden kötü?
Encapsulation yok, bütün alanlar public.
java.time API yerine Date, Duration yok.
Model içinde SQL (katman ihlali).
Raw types, Comparable yanlış, magic string/sahalar.
Annotation göstermelik.
 */

// Kötü: Model içinde bambaşka sorumluluklar tutulacak
@CommonAudited("appointment")
public class ModelAppointment implements Comparable { // Kötü: raw Comparable
    public Long id;
    public Long patientId;
    public Long doctorId;
    public Date start;           // Kötü: java.time yerine Date
    public int minutes;          // Kötü: Duration yok
    public ModelAppointmentStatus status = ModelAppointmentStatus.PENDING;

    // Kötü: model içinde SQL tutmak (tamamen yanlış katmanlama)
    public static final String CREATE_SQL = """
        CREATE TABLE IF NOT EXISTS APPOINTMENT(
          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
          PATIENT_ID BIGINT,
          DOCTOR_ID BIGINT,
          START_TS TIMESTAMP,
          MINUTES INT,
          STATUS VARCHAR(20)
        );
        """;

    // Kötü: gereksiz ctor, validation yok
    public ModelAppointment(Long id, Long patientId, Long doctorId, Date start, int minutes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.start = start;
        this.minutes = minutes;
    }

    // Kötü: raw Comparable — ClassCastException riski
    @Override public int compareTo(Object o) {
        if (o instanceof ModelAppointment a) { // Pattern matching burada gereksiz
            return this.start.compareTo(a.start);
        }
        return 0; // Kötü: sessizce 0 dönmek
    }

    // Kötü: anlamsız iç sınıf
    public static class StartDescComparator implements Comparator<ModelAppointment> {
        @Override public int compare(ModelAppointment a1, ModelAppointment a2) {
            return a2.start.compareTo(a1.start);
        }
    }
}
