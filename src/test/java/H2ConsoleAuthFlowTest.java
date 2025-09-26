import com.hamitmizrak.good.config.Migrations;
import com.hamitmizrak.good.domain.Role;
import com.hamitmizrak.good.dto.LoginRequest;
import com.hamitmizrak.good.dto.RegisterRequest;
import com.hamitmizrak.good.repository.UserRepository;
import com.hamitmizrak.good.security.SessionContext;
import com.hamitmizrak.good.service.AuthService;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class H2ConsoleAuthFlowTest {

    // Aynı URL
    private static final String FILE_DB_URL = "jdbc:h2:file:./database_memory_persist/hospital;AUTO_SERVER=TRUE";

    private static UserRepository userRepo;
    private static AuthService auth;

    @BeforeAll
    static void init() throws Exception {
        // Testte DB URL'yi sabitle
        setDbUrlUnsafe(FILE_DB_URL);

        // Şemaları oluştur
        Migrations.migrate();

        userRepo = new UserRepository();
        auth = new AuthService(userRepo, Locale.getDefault());
        assertNotNull(userRepo);
        assertNotNull(auth);
    }

    @AfterEach
    void tearDown() {
        SessionContext.logout();
    }

    @Test
    @Order(1)
    @DisplayName("Users boşsa register, varsa login — session oluşur")
    void ensureUserAndLogin() throws Exception {
        final String username = "demo";
        final String email    = "demo@example.com";
        final String password = "pass123";

        if (!userExists(username)) {
            long id = auth.register(new RegisterRequest(username, email, password), Role.PATIENT);
            assertTrue(id > 0, "Register başarısız olmamalı");
        }

        boolean ok = auth.login(new LoginRequest(username, password));
        assertTrue(ok, "Login başarılı olmalı");
        assertTrue(SessionContext.current().isPresent(), "Session oluşmalı");
        assertEquals(username, SessionContext.current().get().username());
    }

    @Test
    @Order(2)
    @DisplayName("Kayıt önerisi → Kayıt → Login → Listele → Çık")
    void runMainConsoleMenuOnce2() throws Exception {
        // 1) Önce tüm kullanıcıları sil: açılışta MainConsole kayıt önersin
        try (var c = java.sql.DriverManager.getConnection(FILE_DB_URL, "sa", "");
             var s = c.createStatement()) {
            s.execute("DELETE FROM USERS");
        }

        // 2) Konsola otomatik input veriyoruz. Sıra:
        //    - "e" (kayıt olmak istiyorum)
        //    - "demo2" (username)
        //    - "demo2@example.com" (email)
        //    - "pass123" (parola)
        //    - "pass123" (parola tekrar)
        //    - "" (rol boş => PATIENT)
        //    - "demo2" (login username)
        //    - "pass123" (login password)
        //    - "2" (menü: Randevu Listele)
        //    - "" "" "" (üç filtreyi boş geç → hepsini listele)
        //    - "0" (çıkış)
        String scriptedInput = String.join(System.lineSeparator(),
                "e",
                "demo2",
                "demo2@example.com",
                "pass123",
                "pass123",
                "",
                "demo2",
                "pass123",
                "2",
                "",
                "",
                "",
                "0"
        ) + System.lineSeparator();

        // 3) Çıktıyı da doğrulamak için System.out yakalıyoruz
        var originalIn  = System.in;
        var originalOut = System.out;
        var in  = new java.io.ByteArrayInputStream(scriptedInput.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var bos = new java.io.ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new java.io.PrintStream(bos, true, java.nio.charset.StandardCharsets.UTF_8));

        try {
            new com.hamitmizrak.good.menu.MainConsole(); // Bloklar; "0" ile menüden çıkınca biter
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        // 4) Akış doğrulamaları (istenirse daha spesifik assert'ler ekleyebilirsiniz)
        String out = bos.toString(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(out.contains("Sistemde kayıtlı kullanıcı bulunamadı"), "Register önerisi görünmeli");
        assertTrue(out.contains("=== Kayıt (Register) ==="), "Kayıt ekranı gelmeli");
        assertTrue(out.contains("=== Giriş (Login) ==="), "Login ekranı gelmeli");
        assertTrue(out.contains("========== MENÜ =========="), "Menü gelmeli");
        assertTrue(out.contains("=== Randevu Listele ==="), "Listeleme ekranı gelmeli");
        assertTrue(out.contains("Çıkış yapıldı"), "Çıkış mesajı görünmeli");
    }



    @Test
    @Order(3)
    void runMainConsoleMenuOnce3() throws Exception {
        try (var c = java.sql.DriverManager.getConnection(FILE_DB_URL, "sa", "");
             var s = c.createStatement()) {
            s.execute("DELETE FROM USERS");  // -> MainConsole açılışta register önerecek
        }

        System.setIn(new ByteArrayInputStream(String.join(System.lineSeparator(),
                "e",              // Kayıt olmak ister misiniz? (E/h) -> evet
                "demo",           // username
                "demo@example.com",
                "pass123",        // parola
                "pass123",        // tekrar
                "",               // rol boş -> PATIENT
                "demo",           // login username
                "pass123",        // login password
                "0"               // menüden çık
        ).concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));

        new com.hamitmizrak.good.menu.MainConsole();
    }


    @Test
    @Order(4)
    @DisplayName("MainConsole açılır → login olur → menü gösterir → whoAmI → çıkış")
    void runMainConsoleMenuOnce4() throws Exception {
        // Konsola otomatik input veriyoruz:
        //  - username
        //  - password
        //  - "6" (Ben Kimim)
        //  - "0" (Çık)
        String scriptedInput = String.join(System.lineSeparator(),
                "demo",            // kullanıcı adı
                "pass123",         // parola
                "6",               // Ben Kimim
                "0"                // Çık
        ) + System.lineSeparator();

        // ConsoleIO Scanner'ı System.in'i kullandığı için, sınıf yüklenmeden önce in'i ayarlıyoruz.
        System.setIn(new ByteArrayInputStream(scriptedInput.getBytes(StandardCharsets.UTF_8)));

        // MainConsole çalıştır (bloklayıcıdır; "0" ile çıkacaktır)
        new com.hamitmizrak.good.menu.MainConsole();

        // Buraya sorunsuz döndüysek test başarılıdır (exception yok).
        assertTrue(true);
    }

    // ---------- Yardımcılar ----------

    private static void setDbUrlUnsafe(String url) throws Exception {
        Class<?> db = Class.forName("com.hamitmizrak.good.config.DB");
        Method m = db.getDeclaredMethod("setUrlUnsafe", String.class);
        m.setAccessible(true);
        m.invoke(null, url);
    }

    private boolean userExists(String username) throws SQLException {
        try (Connection c = DriverManager.getConnection(FILE_DB_URL, "sa", "");
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM USERS WHERE USERNAME=? LIMIT 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
