package bad.repository;

import com.hamitmizrak.bad.model.Appointment;
import com.hamitmizrak.bad.model.AppointmentStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


/*
 ❌ KÖTÜ:Ne yapıyor? H2 bağlantısı, tablo oluşturma, CRUD benzeri işlemler.
Neden kötü?
DriverManager her çağrıda yeni bağlantı (pool yok).
Statement + string birleştirme (SQL injection).
Kaynak kapatma eksik, try-with-resources çoğu yerde yok.
Hatalar yutuluyor veya printStackTrace.
Transaction yok, SRP yok (her şey tek sınıfta).
I/O ve config işini burada yapıp katman karıştırıyor.
 */

/*
❌ KÖTÜ Bu modülün özeti – neden kötü?
SQL injection riski, kaynak sızıntıları, transaction yok.
Katman ihlali (model SQL’e bağımlı; repo dosya I/O yapıyor).
Null/sihrî dönüşler, hata yönetimi yok.
*/

public class AppointmentRepository {

    // Kötü: Hard-coded config, güvenlik yok
    public static String URL = "jdbc:h2:~/hospital_bad_db;AUTO_SERVER=TRUE";
    public static String USER = "sa";
    public static String PASS = "";

    // Kötü: tek bir util format, thread-unsafe SimpleDateFormat
    private static final SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Kötü: connection her seferinde aç-kapat, bazen kapatma yok
    private static Connection conn() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace(); // Kötü: doğru log yok
            return null;
        }
    }

    // Kötü: repository içinde dosya okuma ve rastgele yan görevler
    public static void init() {
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.execute(Appointment.CREATE_SQL); // Kötü: Model içindeki SQL’e bağımlılık
            // Kötü: anlamsız dosya okuma (I/O karışmış)
            Path p = Path.of(System.getProperty("user.home"), "hospital-note.txt");
            if (!Files.exists(p)) Files.writeString(p, "created: " + new Date());
        } catch (Exception e) {
            // Kötü: sessiz
        }
    }

    // Kötü: PreparedStatement yok, injection mümkün
    public static Long save(Appointment a) {
        String sql = "INSERT INTO APPOINTMENT(PATIENT_ID,DOCTOR_ID,START_TS,MINUTES,STATUS) VALUES(" +
                a.patientId + "," + a.doctorId + ", TIMESTAMP '" + F.format(a.start) + "'," +
                a.minutes + ",'" + a.status.name() + "');";
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Kötü: log yok, sınıflandırma yok
        }
        return null; // Kötü: null dönmek
    }

    public static List<Appointment> findAll() {
        List<Appointment> list = new ArrayList();
        try {
            Connection c = conn(); // Kötü: try-with-resources yok
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM APPOINTMENT ORDER BY START_TS");
            while (rs.next()) {
                // Builder design göre yazıldı
                // KÖTÜ PRATİK: Parametre sırası hatasına açık, zorunlu alan garantisi yok,
                // kapsülleme (encapsulation) ihlali: a.status alanına doğrudan erişim ❌
                // EKİSİ ❌
                /*Appointment a = new Appointment(
                        rs.getLong("ID"),
                        rs.getLong("PATIENT_ID"),
                        rs.getLong("DOCTOR_ID"),
                        rs.getTimestamp("START_TS"),
                        rs.getInt("MINUTES")
                );
                a.status = AppointmentStatus.valueOf(rs.getString("STATUS"));
                list.add(a)*/;
            }
            // Kötü: kapatmalar eksik (sızıntı riski)
        } catch (SQLException e) {
            // Kötü: yut
        }
        // Kötü: gereksiz stream kullanımı
        list.parallelStream().forEach(x -> {}); // Hiçbir şey yapmıyor
        return list;
    }

    public static int updateStatus(Long id, AppointmentStatus stt) {
        String sql = "UPDATE APPOINTMENT SET STATUS='" + stt.name() + "' WHERE ID=" + id;
        try (Connection c = conn(); Statement st = c.createStatement()) {
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            return -1; // Kötü: anlamsız kod
        }
    }

    public static int deleteById(Long id) {
        try (Connection c = conn(); Statement st = c.createStatement()) {
            return st.executeUpdate("DELETE FROM APPOINTMENT WHERE ID=" + id);
        } catch (SQLException e) {
            return 0;
        }
    }
}
