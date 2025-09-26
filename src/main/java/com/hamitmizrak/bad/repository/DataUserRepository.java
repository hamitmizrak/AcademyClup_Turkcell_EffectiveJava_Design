package com.hamitmizrak.bad.repository;

import com.hamitmizrak.bad.auth.AuthRole;
import com.hamitmizrak.bad.auth.AuthUser;
import java.sql.*;

public class DataUserRepository {

    // Kötü: AppointmentRepository'deki config'i tekrar etmiyoruz, doğrudan yazıyoruz.
    private static String URL = "jdbc:h2:~/hospital_bad_db;AUTO_SERVER=TRUE";
    private static String USER = "sa";
    private static String PASS = "";

    public static void init() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.execute(AuthUser.CREATE_SQL); // Kötü: model içindeki SQL
        } catch (Exception ignored) {}
    }

    // Kötü: PreparedStatement yok → injection riski
    public static Long save(AuthUser u) {
        String sql = "INSERT INTO USERS(USERNAME,PASSWORD,ROLE,NAME,NATIONAL_ID) VALUES('" +
                u.username + "','" + u.password + "','" + u.role.name() + "','" + u.name + "','" + u.nationalId + "')";
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            try (ResultSet rs = st.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
        } catch (SQLException ignored) {}
        return null;
    }

    public static AuthUser findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password + "'";
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                AuthUser u = new AuthUser();
                u.id = rs.getLong("ID");
                u.username = rs.getString("USERNAME");
                u.password = rs.getString("PASSWORD");
                u.role = AuthRole.valueOf(rs.getString("ROLE"));
                u.name = rs.getString("NAME");
                u.nationalId = rs.getString("NATIONAL_ID");

                return u;
            }
        } catch (SQLException ignored) {

        } finally {
            // c.close();
        }
        return null; // Kötü: Optional yerine null
    }
}
