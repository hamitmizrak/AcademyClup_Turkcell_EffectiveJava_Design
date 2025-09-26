package com.hamitmizrak.good.repository;

/*
 CLEAN-UP
 ✅ existsById, findById (no-args + setter), insert
 ✅ list(): tüm hastaları döner
*/

import com.hamitmizrak.good.domain.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepository extends BaseRepository {

    public boolean existsById(long id) throws SQLException {
        final String sql = "SELECT 1 FROM PATIENTS WHERE ID=? LIMIT 1";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<Patient> findById(long id) throws SQLException {
        final String sql = "SELECT ID, NAME, NATIONAL_ID, PHONE, EMAIL FROM PATIENTS WHERE ID=?";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Patient p = new Patient();
                p.setId(rs.getLong("ID"));
                p.setName(rs.getString("NAME"));
                p.setNationalId(rs.getString("NATIONAL_ID"));
                p.setPhone(rs.getString("PHONE"));
                p.setEmail(rs.getString("EMAIL"));
                return Optional.of(p);
            }
        }
    }

    public long insert(String name, String nationalId, String phone, String email) throws SQLException {
        final String sql = "INSERT INTO PATIENTS(NAME, NATIONAL_ID, PHONE, EMAIL) VALUES (?,?,?,?)";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, nationalId);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) {
                return rk.next() ? rk.getLong(1) : 0L;
            }
        }
    }

    /** Tüm hastaları listele. */
    public List<PatientRow> list() throws SQLException {
        final String sql = "SELECT ID, NAME, NATIONAL_ID, PHONE, EMAIL FROM PATIENTS ORDER BY ID";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<PatientRow> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new PatientRow(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getString("NATIONAL_ID"),
                        rs.getString("PHONE"),
                        rs.getString("EMAIL")
                ));
            }
            return out;
        }
    }

    public record PatientRow(long id, String name, String nationalId, String phone, String email) {}
}
