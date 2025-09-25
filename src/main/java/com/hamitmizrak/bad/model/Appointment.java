package com.hamitmizrak.bad.model;

import com.hamitmizrak.bad.common.Audited;

import java.util.Date;

// ❌ KÖTÜ: KOD
/*
Encapsulation yok
java.time API yerine Date kullandık
Model içinde SQL
Raw types Comparable yanlış,
public alan kullanıldı
 */

// @Audited("appoinment") // ❌ KÖTÜ: hiçbir etkisi yok
public class Appointment implements Comparable {

    // Field
    public Long id;     // ❌ KÖTÜ:  public alan
    public Long patientId;// ❌ KÖTÜ:  public alan
    public Long doctorId;// ❌ KÖTÜ:  public alan
    public Date start;// ❌ KÖTÜ:  public alan, java.time
    public int minutes;// ❌ KÖTÜ:  public alan, Duration yok
    public AppointmentStatus status = AppointmentStatus.PENDING;

    // SQL
    public static final String CREATE_SQL = """
            CREATE TABLE IF NOT EXISTS APPOINTMENT(
            ID BIGINT AUTO_INCREMENT PRIMARY KEY,
            PARENT_ID BIGINT,
            DOCTOR_ID BIGINT,
            START_TS TIMESTAMP,
            MINUTES INT,
            STATUS VARCHAR(20)
            )
            """;

    // Parametreli constructor
    // ❌ KÖTÜ: Gereksiz ctor, validation
    public Appointment(Long id, Long patientId, Long doctorId, Date start, int minutes, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.start = start;
        this.minutes = minutes;
        this.status = status;
    }

    // ❌ KÖTÜ: raw Comparable - ClassCastException Risk
    @Override
    public int compareTo(Object o) {  // ❌ KÖTÜ: fieldName
        if(o instanceof Appointment a){
            return this.start.compareTo(a.start);
        }
        return 0;  // ❌ KÖTÜ: o dönmek bir yerde anlamsız olabilir.
    }
}
