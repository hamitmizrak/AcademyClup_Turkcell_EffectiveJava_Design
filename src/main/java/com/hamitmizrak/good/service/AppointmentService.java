package com.hamitmizrak.good.service;

/*
 CLEAN-UP
 ✅ Randevudan önce hasta/doktor var mı kontrolü (FK güvenliği)
*/

import com.hamitmizrak.good.domain.Appointment;
import com.hamitmizrak.good.domain.AppointmentStatus;
import com.hamitmizrak.good.dto.AppointmentCreateRequest;
import com.hamitmizrak.good.dto.AppointmentListFilter;
import com.hamitmizrak.good.dto.AppointmentStatusUpdateRequest;
import com.hamitmizrak.good.repository.AppointmentRepository;
import com.hamitmizrak.good.repository.DoctorRepository;
import com.hamitmizrak.good.repository.PatientRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AppointmentService {
    private final AppointmentRepository repo;
    private final PatientRepository patients;
    private final DoctorRepository doctors;

    public AppointmentService(AppointmentRepository repo,
                              PatientRepository patients,
                              DoctorRepository doctors) {
        this.repo = Objects.requireNonNull(repo);
        this.patients = Objects.requireNonNull(patients);
        this.doctors = Objects.requireNonNull(doctors);
    }

    public long create(AppointmentCreateRequest r) throws SQLException {
        Objects.requireNonNull(r, "request null");
        if (r.dateTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Randevu geçmiş tarihe olamaz");

        if (!patients.existsById(r.patientId()))
            throw new IllegalArgumentException("Hasta bulunamadı (ID=" + r.patientId() + ")");

        if (!doctors.existsById(r.doctorId()))
            throw new IllegalArgumentException("Doktor bulunamadı (ID=" + r.doctorId() + ")");

        Appointment a = Appointment.builder()
                .patientId(r.patientId())
                .doctorId(r.doctorId())
                .dateTime(r.dateTime())
                .status(AppointmentStatus.PENDING)
                .note(r.note())
                .build();
        return repo.insert(a);
    }

    public List<Appointment> list(AppointmentListFilter f) throws SQLException {
        return repo.list(f.doctorId(), f.patientId(), f.status());
    }

    public boolean updateStatus(AppointmentStatusUpdateRequest r) throws SQLException {
        return repo.updateStatus(r.appointmentId(), r.newStatus());
    }

    public boolean deleteHard(long id) throws SQLException {
        return repo.deleteHard(id);
    }
}
