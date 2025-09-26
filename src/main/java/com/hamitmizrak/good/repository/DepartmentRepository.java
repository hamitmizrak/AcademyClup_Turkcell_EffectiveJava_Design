package com.hamitmizrak.good.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentRepository extends BaseRepository {

    public long insert(String name) throws SQLException {
        final String sql = "INSERT INTO DEPARTMENTS(NAME) VALUES (?)";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) {
                return rk.next() ? rk.getLong(1) : 0L;
            }
        }
    }

    public boolean existsById(long id) throws SQLException {
        final String sql = "SELECT 1 FROM DEPARTMENTS WHERE ID=? LIMIT 1";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<DeptRow> findById(long id) throws SQLException {
        final String sql = "SELECT ID, NAME FROM DEPARTMENTS WHERE ID=?";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new DeptRow(rs.getLong("ID"), rs.getString("NAME")));
            }
        }
    }

    public List<DeptRow> list() throws SQLException {
        final String sql = "SELECT ID, NAME FROM DEPARTMENTS ORDER BY ID";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<DeptRow> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new DeptRow(rs.getLong("ID"), rs.getString("NAME")));
            }
            return out;
        }
    }

    public record DeptRow(long id, String name) {}
}
