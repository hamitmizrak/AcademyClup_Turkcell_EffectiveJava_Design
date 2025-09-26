package com.hamitmizrak.good.repository;

/*
 CLEAN-UP
 ✅ existsById, isAvailable, updateAvailability, insert, list
 ✅ listAvailable(): AVAILABLE=TRUE filtreli
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorRepository extends BaseRepository {

    public boolean existsById(long id) throws SQLException {
        final String sql = "SELECT 1 FROM DOCTORS WHERE ID=? LIMIT 1";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<Boolean> isAvailable(long id) throws SQLException {
        final String sql = "SELECT AVAILABLE FROM DOCTORS WHERE ID=?";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getBoolean(1));
                return Optional.empty();
            }
        }
    }

    public boolean updateAvailability(long id, boolean available) throws SQLException {
        final String sql = "UPDATE DOCTORS SET AVAILABLE=?, UPDATED_AT=CURRENT_TIMESTAMP WHERE ID=?";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    public long insert(String name, long departmentId, boolean available) throws SQLException {
        final String sql = "INSERT INTO DOCTORS(NAME, DEPARTMENT_ID, AVAILABLE) VALUES (?,?,?)";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setLong(2, departmentId);
            ps.setBoolean(3, available);
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) {
                return rk.next() ? rk.getLong(1) : 0L;
            }
        }
    }

    public List<DocRow> list() throws SQLException {
        final String sql = "SELECT ID, NAME, DEPARTMENT_ID, AVAILABLE FROM DOCTORS ORDER BY ID";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<DocRow> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new DocRow(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getLong("DEPARTMENT_ID"),
                        rs.getBoolean("AVAILABLE")
                ));
            }
            return out;
        }
    }

    public List<DocRow> listAvailable() throws SQLException {
        final String sql = "SELECT ID, NAME, DEPARTMENT_ID, AVAILABLE FROM DOCTORS WHERE AVAILABLE=TRUE ORDER BY ID";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<DocRow> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new DocRow(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getLong("DEPARTMENT_ID"),
                        rs.getBoolean("AVAILABLE")
                ));
            }
            return out;
        }
    }

    public record DocRow(long id, String name, long departmentId, boolean available) {}
}
