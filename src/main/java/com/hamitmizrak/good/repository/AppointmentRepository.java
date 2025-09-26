package com.hamitmizrak.good.repository;

/*
 CLEAN-UP ÖZETİ
 ✅ PreparedStatement + try-with-resources.
 ✅ Listeleme için dinamik filtre (doctor/patient/status).
 ✅ Hard delete, status update ve map(ResultSet) tek yerde.
 ✅ Effective Java: gereksiz paylaşımlardan kaçınılır; dönüşler minimal-kapsamlı.
*/

import com.hamitmizrak.good.domain.Appointment;
import com.hamitmizrak.good.domain.AppointmentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentRepository extends BaseRepository {

    public long insert(Appointment a) throws SQLException {
        String sql = """
                    INSERT INTO APPOINTMENTS(PATIENT_ID,DOCTOR_ID,DATETIME,STATUS,NOTE,VERSION)
                    VALUES(?,?,?,?,?,0)
                """;
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getPatientId());
            ps.setLong(2, a.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getDateTime()));
            ps.setString(4, a.getStatus().name());
            ps.setString(5, a.getNote());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                long id = rs.next() ? rs.getLong(1) : 0L;
                a.setId(id);
                return id;
            }
        }
    }

    public List<Appointment> list(Long doctorId, Long patientId, AppointmentStatus status) throws SQLException {
        StringBuilder sb = new StringBuilder("""
                    SELECT ID,PATIENT_ID,DOCTOR_ID,DATETIME,STATUS,NOTE,VERSION
                    FROM APPOINTMENTS WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (doctorId != null) {
            sb.append(" AND DOCTOR_ID=?");
            params.add(doctorId);
        }
        if (patientId != null) {
            sb.append(" AND PATIENT_ID=?");
            params.add(patientId);
        }
        if (status != null) {
            sb.append(" AND STATUS=?");
            params.add(status.name());
        }
        sb.append(" ORDER BY DATETIME DESC");

        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Appointment> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public boolean updateStatus(long id, AppointmentStatus newStatus) throws SQLException {
        String sql = "UPDATE APPOINTMENTS SET STATUS=?, UPDATED_AT=CURRENT_TIMESTAMP, VERSION=VERSION+1 WHERE ID=?";
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteHard(long id) throws SQLException {
        String sql = "DELETE FROM APPOINTMENTS WHERE ID=?";
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Appointment> findById(long id) throws SQLException {
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM APPOINTMENTS WHERE ID=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    // İYİ PRATİK (Builder Pattern)
    // - Okunabilirlik ↑: Alan isimleriyle kurulum
    // - Güvenlik ↑: build() zorunlu alanları doğrular (fail-fast)
    // - Kapsülleme ↑: a.status = ... gibi doğrudan alan erişimi yok
    private Appointment map(ResultSet resultSet) throws SQLException {
        Appointment appointment = Appointment.builder()
                .patientId(resultSet.getLong("PATIENT_ID"))
                .doctorId(resultSet.getLong("DOCTOR_ID"))
                .dateTime(resultSet.getTimestamp("DATETIME").toLocalDateTime())
                .status(AppointmentStatus.valueOf(resultSet.getString("STATUS")))
                .note(resultSet.getString("NOTE"))
                .build();
        appointment.setId(resultSet.getLong("ID"));
        //appointment.setVersion(resultSet.getInt("VERSION"));
        return appointment;
    }
}
