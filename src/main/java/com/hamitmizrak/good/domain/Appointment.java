package com.hamitmizrak.good.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/*
  Appointment — FINAL (Builder + version)
  ---------------------------------------
  NEDEN version?
  - "Optimistic Locking" için sürüm alanı. Güncellemede WHERE ... AND VERSION=? ile çakışmaları yakalar,
    başarıda VERSION=VERSION+1 yaparız. Böylece eşzamanlı güncellemelerde veri kaybını önleriz.

  PROJEYE ETKİ:
  - Domain modeli kapsülleme ile tutulur; Builder ile fail-fast doğrulama yapılır.
  - version int alanı default 0’dır; repo insert sonrası 0 başlatılır, update’lerde artar.
*/

// Builder:
/*
1- parametreli ctor private oslun
2- Innerclass Builder (Constant)
* */
public class Appointment {

    // — Kalıcılık alanları —
    private Long id;            // insert sonrası atanır
    private int version;        // optimistic locking sürümü (>=0)

    // — Zorunlu alanlar —
    private final long patientId;
    private final long doctorId;
    private final LocalDateTime dateTime;
    private AppointmentStatus status;

    // — Opsiyonel alanlar —
    private final String note;

    // — Yalnızca Builder kullanabilsin diye private ctor —
    private Appointment(Builder builder) {
        this.id        = builder.id;
        this.version   = builder.version;
        this.patientId = builder.patientId;
        this.doctorId  = builder.doctorId;
        this.dateTime  = builder.dateTime;
        this.status    = builder.status;
        this.note      = builder.note;
    }

    // — Builder giriş noktası —
    public static Builder builder() { return new Builder(); }

    // — BUILDER (INNER CLASS)—
    public static final class Builder {
        private Long id;                 // çoğunlukla null
        private int version = 0;         // varsayılan 0
        private long patientId;          // required
        private long doctorId;           // required
        private LocalDateTime dateTime;  // required
        private AppointmentStatus status;// required
        private String note;             // optional

        // parametresiz Constructor (private)
        private Builder() {}

        // parametreli Constructor (private)
        public Builder id(Long id) {
            this.id = id; return this;
        }

        public Builder version(int version) { this.version = version; return this; }
        public Builder patientId(long patientId) { this.patientId = patientId; return this; }
        public Builder doctorId(long doctorId) { this.doctorId = doctorId; return this; }
        public Builder dateTime(LocalDateTime dateTime) { this.dateTime = dateTime; return this; }
        public Builder status(AppointmentStatus status) { this.status = status; return this; }
        public Builder note(String note) { this.note = note; return this; }

        public Appointment build() {
            if (patientId <= 0) throw new IllegalArgumentException("patientId > 0 olmalı");
            if (doctorId  <= 0) throw new IllegalArgumentException("doctorId > 0 olmalı");
            if (dateTime == null) throw new IllegalArgumentException("dateTime zorunludur");
            if (status   == null) throw new IllegalArgumentException("status zorunludur");
            if (version  < 0)     throw new IllegalArgumentException("version negatif olamaz");
            return new Appointment(this);
        }
    }

    // — equals/hashCode: id varsa id’ye göre; yoksa doğal anahtar —
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment that)) return false;
        if (id != null && that.id != null) return id.equals(that.id);
        return doctorId == that.doctorId
                && patientId == that.patientId
                && Objects.equals(dateTime, that.dateTime);
    }

    // hashCode
    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : Objects.hash(doctorId, patientId, dateTime);
    }

    // toString
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", version=" + version +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", dateTime=" + dateTime +
                ", status=" + status +
                ", note='" + note + '\'' +
                '}';
    }

    // — GETTER’lar —
    public Long getId() { return id; }
    public int getVersion() { return version; }
    public long getPatientId() { return patientId; }
    public long getDoctorId() { return doctorId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public AppointmentStatus getStatus() { return status; }
    public String getNote() { return note; }

    // — SETTER (kalıcılık/iş akışı için kontrollü) —
    public void setId(Long id) { this.id = id; }
    public void setVersion(int version) {
        if (version < 0) throw new IllegalArgumentException("version negatif olamaz");
        this.version = version;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = Objects.requireNonNull(status, "status zorunludur");
    }
}
