package com.hamitmizrak.bad.repository;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DataDoctorRepository {
    private static String URL = "jdbc:h2:~/hospital_bad_db;AUTO_SERVER=TRUE";
    private static String USER = "sa";
    private static String PASS = "";

    // Kötü: paylaşılan state, senkron yok
    public static Map<Long, Boolean> AVAIL_CACHE = new HashMap<>();

    public static void init() {
        String sql = """
        CREATE TABLE IF NOT EXISTS DOCTOR(
          ID BIGINT PRIMARY KEY,
          NAME VARCHAR(128),
          SPECIALIZATION VARCHAR(64),
          AVAILABLE BOOLEAN
        );
        """;
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (Exception ignored) {}
    }

    public static int upsertDoctor(long id, String name, String spec, boolean available) {
        // Kötü: naive UPSERT
        String del = "DELETE FROM DOCTOR WHERE ID=" + id;
        String ins = "INSERT INTO DOCTOR(ID,NAME,SPECIALIZATION,AVAILABLE) VALUES(" + id + ",'" + name + "','" + spec + "'," + available + ")";
        try (Connection c = DriverManager.getConnection(URL, USER, PASS); Statement s = c.createStatement()) {
            s.executeUpdate(del);
            int x = s.executeUpdate(ins);
            AVAIL_CACHE.put(id, available); // Kötü: senkron yok
            return x;
        } catch (SQLException e) { return -1; }
    }

    public static int setAvailability(long id, boolean available) {
        String sql = "UPDATE DOCTOR SET AVAILABLE=" + available + " WHERE ID=" + id;
        try (Connection c = DriverManager.getConnection(URL, USER, PASS); Statement s = c.createStatement()) {
            int x = s.executeUpdate(sql);
            AVAIL_CACHE.put(id, available);
            return x;
        } catch (SQLException e) { return -1; }
    }
}
