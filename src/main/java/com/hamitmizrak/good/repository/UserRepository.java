package com.hamitmizrak.good.repository;

/*
 CLEAN-UP ÖZETİ
 ✅ findByUsernameOrEmail eklendi (LOWER(...) ile case-insensitive)
*/

import com.hamitmizrak.good.domain.Role;

import java.sql.*;
import java.util.Optional;

public class UserRepository extends BaseRepository {

    public long insert(String username, String email, String passwordHash, Role role) throws SQLException {
        String sql = "INSERT INTO USERS(USERNAME,EMAIL,PASSWORD_HASH,ROLE,ENABLED) VALUES(?,?,?,?,TRUE)";
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public Optional<UserRow> findByUsername(String username) throws SQLException {
        String sql = "SELECT ID, USERNAME, EMAIL, PASSWORD_HASH, ROLE, ENABLED FROM USERS WHERE USERNAME=?";
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Optional<UserRow> findByEmail(String email) throws SQLException {
        String sql = "SELECT ID, USERNAME, EMAIL, PASSWORD_HASH, ROLE, ENABLED FROM USERS WHERE EMAIL=?";
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    /** Username veya Email ile tek sorguda bul (case-insensitive). */
    public Optional<UserRow> findByUsernameOrEmail(String identifier) throws SQLException {
        String sql = """
                SELECT ID, USERNAME, EMAIL, PASSWORD_HASH, ROLE, ENABLED
                  FROM USERS
                 WHERE LOWER(USERNAME)=LOWER(?) OR LOWER(EMAIL)=LOWER(?)
                """;
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public long count() throws SQLException {
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(1) FROM USERS");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    private UserRow map(ResultSet rs) throws SQLException {
        return new UserRow(
                rs.getLong("ID"),
                rs.getString("USERNAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD_HASH"),
                Role.valueOf(rs.getString("ROLE")),
                rs.getBoolean("ENABLED")
        );
    }

    public record UserRow(long id, String username, String email, String passwordHash, Role role, boolean enabled){}
}
