package com.hamitmizrak.good.menu;

import com.hamitmizrak.good.config.Migrations;
import com.hamitmizrak.good.domain.Appointment;
import com.hamitmizrak.good.domain.AppointmentStatus;
import com.hamitmizrak.good.domain.Role;
import com.hamitmizrak.good.dto.*;
import com.hamitmizrak.good.repository.*;
import com.hamitmizrak.good.security.SessionContext;
import com.hamitmizrak.good.service.AppointmentService;
import com.hamitmizrak.good.service.AuthService;
import com.hamitmizrak.good.util.ConsoleIO;
import com.hamitmizrak.good.util.Validators;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class MainConsole {

    private final UserRepository userRepo = new UserRepository();
    private final AppointmentRepository apptRepo = new AppointmentRepository();
    private final DoctorRepository doctorRepo = new DoctorRepository();
    private final PatientRepository patientRepo = new PatientRepository();
    private final DepartmentRepository deptRepo = new DepartmentRepository();

    private final AppointmentService appts = new AppointmentService(apptRepo, patientRepo, doctorRepo);
    private final AuthService auth = new AuthService(userRepo, Locale.getDefault());

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MainConsole() throws Exception { run(); }

    public void run() throws Exception {
        Migrations.migrate();

        long count = userRepo.count();
        if (count == 0) {
            System.out.println("\n*** Sistemde kayıtlı kullanıcı bulunamadı. ***");
            String ans = ConsoleIO.ask("Kayıt olmak ister misiniz? (E/h): ").trim().toLowerCase();
            if (ans.isEmpty() || ans.startsWith("e")) {
                handleRegisterFlow();
                System.out.println("Kayıt tamam. Şimdi giriş yapalım.");
                if (!handleLoginFlow()) {
                    System.out.println("Giriş başarısız. Uygulama sonlandırılıyor.");
                    return;
                }
            } else {
                System.out.println("Kayıt yapılmadı. Uygulamadan çıkılıyor.");
                return;
            }
        } else {
            System.out.println("\n*** Hoş geldiniz! Lütfen giriş yapınız. ***");
            if (!handleLoginFlow()) {
                System.out.println("Giriş başarısız. Uygulama sonlandırılıyor.");
                return;
            }
        }

        menuLoop();
    }

    private Role askRoleChoice() {
        System.out.println("""
        Rol Seçiniz (boş geç: PATIENT):
         1) PATIENT  (hasta)
         2) DOCTOR   (doktor)
         3) ADMIN    (yönetici)
        """);

        while (true) {
            String in = ConsoleIO.ask("Seçim: ").trim().toLowerCase();

            switch (in) {
                // PATIENT
                case "1", "p", "patient", "hasta" -> {
                    return Role.PATIENT;
                }
                // DOCTOR
                case "2", "d", "doctor", "doktor" -> {
                    return Role.DOCTOR;
                }
                // ADMIN
                case "3", "a", "admin", "yönetici", "yonetici" -> {
                    return Role.ADMIN;
                }
                // boş giriş => varsayılan PATIENT
                case "" -> {
                    System.out.println("Varsayılan rol seçildi: PATIENT");
                    return Role.PATIENT;
                }
                // geçersiz giriş
                default -> System.out.println("Geçersiz seçim. (1: PATIENT, 2: DOCTOR, 3: ADMIN)");
            }
        }
    }


    // ---------- Akışlar ----------
    private void handleRegisterFlow() throws SQLException {
        System.out.println("\n=== Kayıt (Register) ===");
        String username = askUsername();
        String email = askEmail();
        String password = askStrongPasswordTwice();
        Role role = askRoleChoice();
        long id = auth.register(new RegisterRequest(username, email, password), role);
        System.out.println("Kayıt başarılı. ID=" + id);
    }


    /** identifier email ise email’i, değilse username’i ön-doldurup kayıt akışını yürütür. */
    private void handleRegisterFlowPrefill(String identifier) throws SQLException {
        System.out.println("\n=== Kayıt (Register) ===");

        String username;
        String email;

        if (Validators.email(identifier)) {
            email = identifier;
            System.out.println("E-posta tespit edildi: " + email);
            username = askUsername();
        } else {
            username = identifier;
            System.out.println("Kullanıcı adı tespit edildi: " + username);
            email = askEmail();
        }

        String password = askStrongPasswordTwice();
        Role role = askRoleChoice();

        try {
            long id = auth.register(new RegisterRequest(username, email, password), role);
            System.out.println("Kayıt başarılı. ID=" + id);
        } catch (IllegalArgumentException ex) {
            System.out.println("Kayıt başarısız: " + ex.getMessage());
            // Çakışma (username/email mevcut) vb. durumda kullanıcıya yeniden denetebiliriz:
            String again = ConsoleIO.ask("Yeniden denemek ister misiniz? (E/h): ")
                    .trim().toLowerCase();
            if (again.isEmpty() || again.startsWith("e")) {
                handleRegisterFlowPrefill(identifier); // tekrar dene
            }
        }
    }


    private boolean handleLoginFlow() throws SQLException {
        System.out.println("\n=== Giriş (Login) ===");
        for (int i = 1; i <= 3; i++) {
            String identifier = ConsoleIO.ask("Kullanıcı adı veya E-posta: ").trim();

            // Kullanıcı var mı? (username OR email)
            boolean exists = userRepo.findByUsernameOrEmail(identifier).isPresent();
            if (!exists) {
                System.out.println("Kayıt bulunamadı: " + identifier);
                String ans = ConsoleIO.ask("Kayıt olmak ister misiniz? (E/h): ")
                        .trim().toLowerCase();
                if (ans.isEmpty() || ans.startsWith("e")) {
                    // Kayıt akışına yönlendir (identifier ile ön-doldurma)
                    handleRegisterFlowPrefill(identifier);
                    System.out.println("Kayıt tamam. Şimdi giriş yapalım.");
                    // Deneme haklarını sıfırla (kullanıcıya 3 tam deneme daha ver)
                    i = 0;
                    continue;
                } else {
                    // Kayıt yapılmadı; tekrar identifier iste
                    continue;
                }
            }

            String password = ConsoleIO.askPasswordMasked("Parola: ");
            boolean ok = auth.login(new LoginRequest(identifier, password));
            if (ok) {
                System.out.println("Giriş başarılı. Hoş geldiniz, " + identifier + "!");
                return true;
            }
            System.out.println("Hatalı parola. Kalan deneme: " + (3 - i));
        }
        return false;
    }


    private void menuLoop() throws SQLException {
        while (true) {
            Role role = currentRole();
            printMenu(role);
            String in = ConsoleIO.ask("Seçim: ").trim();

            // Her komutta role kontrolü
            switch (in) {
                case "1" -> createAppointmentFlow();          // herkese açık
                case "2" -> listAppointmentsFlow();           // herkese açık
                case "3" -> updateStatusFlow();               // herkese açık (isterseniz kısıtlayabiliriz)
                case "4" -> deleteAppointmentFlow();          // herkese açık (isterseniz kısıtlayabiliriz)
                case "5" -> checkDoctorAvailabilityFlow();    // herkese açık
                case "6" -> whoAmI();

                case "7" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    createDepartmentFlow();
                }
                case "8" -> {
                    if (!(role == Role.ADMIN || role == Role.DOCTOR)) { deny(); break; }
                    createDoctorFlow();
                }
                case "9" -> {
                    if (!(role == Role.ADMIN || role == Role.PATIENT)) { deny(); break; }
                    createPatientFlow();
                }
                case "10" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    changeDoctorAvailabilityFlow();
                }

                // ADMIN’e özel listeler
                case "11" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    listDoctorsFlow();
                }
                case "12" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    listPatientsFlow();
                }
                case "13" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    listDepartmentsFlow();
                }
                case "14" -> {
                    if (role != Role.ADMIN) { deny(); break; }
                    listAvailableDoctorsFlow();
                }

                case "0" -> {
                    System.out.println("Çıkış yapıldı. Görüşmek üzere!");
                    SessionContext.logout();
                    return;
                }
                default -> System.out.println("Bilinmeyen seçim.");
            }
        }
    }

    private Role currentRole() {
        var sess = SessionContext.current();
        return sess.map(SessionContext.UserSession::role).orElse(Role.PATIENT);
    }

    private void printMenu(Role role) {
        StringBuilder sb = new StringBuilder("""
                
                ========== MENÜ ==========
                1) Randevu Ekle
                2) Randevu Listele
                3) Randevu Durum Güncelle
                4) Randevu Sil (Hard Delete)
                5) Doktor Müsaitliği Kontrol
                6) Ben Kimim
                """);
        // Role-bazlı ekler
        if (role == Role.ADMIN) {
            sb.append("""
                7) Departman Ekle
                8) Doktor Ekle
                9) Hasta Ekle
               10) Doktor Müsaitlik Değiştir (true/false)
               11) Doktorları Listele
               12) Hastaları Listele
               13) Departmanları Listele
               14) Müsait Doktorları Listele
                """);
        } else if (role == Role.DOCTOR) {
            sb.append("""
                8) Doktor Ekle
                """);
        } else if (role == Role.PATIENT) {
            sb.append("""
                9) Hasta Ekle
                """);
        }
        sb.append("""
                0) Çık
                ==========================
                """);
        System.out.print(sb);
    }

    private void deny() { System.out.println("Yetkiniz yok."); }

    // ---------- Menü İşlemleri (CRUD & Yardımcılar) ----------
    private void createDepartmentFlow() throws SQLException {
        System.out.println("\n=== Departman Ekle (ADMIN) ===");
        String name = ConsoleIO.ask("Departman adı: ").trim();
        if (name.isEmpty()) { System.out.println("İşlem iptal: Ad boş olamaz."); return; }
        long id = deptRepo.insert(name);
        System.out.println("Departman oluşturuldu. ID=" + id);
    }

    private void createDoctorFlow() throws SQLException {
        System.out.println("\n=== Doktor Ekle (ADMIN/DOCTOR) ===");

        var deps = deptRepo.list();
        if (deps.isEmpty()) { System.out.println("Henüz departman yok. Önce 'Departman Ekle' yapınız."); return; }
        System.out.println("Mevcut Departmanlar:");
        deps.forEach(d -> System.out.printf("  ID=%d | NAME=%s%n", d.id(), d.name()));

        long deptId = askLong("Department ID: ");
        var depOpt = deptRepo.findById(deptId);
        if (depOpt.isEmpty()) { System.out.println("İşlem iptal: Department bulunamadı (ID=" + deptId + ")"); return; }
        var dep = depOpt.get();
        System.out.printf("Seçilen Departman: ID=%d | NAME=%s%n", dep.id(), dep.name());

        String name = ConsoleIO.ask("Doktor adı: ").trim();
        String ans = ConsoleIO.ask("Müsait mi? (E/h, varsayılan: E): ").trim().toLowerCase();
        boolean available = ans.isEmpty() || ans.startsWith("e");

        long id = doctorRepo.insert(name, deptId, available);
        System.out.println("Doktor oluşturuldu. ID=" + id);
    }

    private void createPatientFlow() throws SQLException {
        System.out.println("\n=== Hasta Ekle (ADMIN/PATIENT) ===");
        String name = ConsoleIO.ask("Ad Soyad: ").trim();
        String nationalId = ConsoleIO.ask("TC No: ").trim();
        String phone = ConsoleIO.ask("Telefon: ").trim();
        String email = ConsoleIO.ask("E-posta: ").trim();
        long id = patientRepo.insert(name, nationalId, phone, email);
        System.out.println("Hasta oluşturuldu. ID=" + id);
    }

    private void changeDoctorAvailabilityFlow() throws SQLException {
        System.out.println("\n=== Doktor Müsaitlik Değiştir (ADMIN) ===");
        long doctorId = askLong("Doktor ID: ");
        if (!doctorRepo.existsById(doctorId)) { System.out.println("Doktor bulunamadı."); return; }
        String ans = ConsoleIO.ask("Yeni durum (E: true / h: false): ").trim().toLowerCase();
        boolean newVal = ans.isEmpty() || ans.startsWith("e");
        boolean ok = doctorRepo.updateAvailability(doctorId, newVal);
        System.out.println(ok ? "Güncellendi." : "Güncellenemedi.");
    }

    private void listDoctorsFlow() throws SQLException {
        System.out.println("\n=== Doktorları Listele (ADMIN) ===");
        var list = doctorRepo.list();
        if (list.isEmpty()) { System.out.println("Kayıt yok."); return; }
        list.forEach(d -> System.out.printf("ID=%d | NAME=%s | DEPT_ID=%d | AVAILABLE=%s%n",
                d.id(), d.name(), d.departmentId(), d.available()));
    }

    private void listAvailableDoctorsFlow() throws SQLException {
        System.out.println("\n=== Müsait Doktorları Listele (ADMIN) ===");
        var list = doctorRepo.listAvailable();
        if (list.isEmpty()) { System.out.println("Kayıt yok."); return; }
        list.forEach(d -> System.out.printf("ID=%d | NAME=%s | DEPT_ID=%d | AVAILABLE=%s%n",
                d.id(), d.name(), d.departmentId(), d.available()));
    }

    private void listPatientsFlow() throws SQLException {
        System.out.println("\n=== Hastaları Listele (ADMIN) ===");
        var list = patientRepo.list();
        if (list.isEmpty()) { System.out.println("Kayıt yok."); return; }
        list.forEach(p -> System.out.printf("ID=%d | NAME=%s | TCKN=%s | PHONE=%s | EMAIL=%s%n",
                p.id(), p.name(), p.nationalId(), p.phone(), p.email()));
    }

    private void listDepartmentsFlow() throws SQLException {
        System.out.println("\n=== Departmanları Listele (ADMIN) ===");
        var list = deptRepo.list();
        if (list.isEmpty()) { System.out.println("Kayıt yok."); return; }
        list.forEach(d -> System.out.printf("ID=%d | NAME=%s%n", d.id(), d.name()));
    }

    private void createAppointmentFlow() throws SQLException {
        System.out.println("\n=== Randevu Ekle ===");
        long patientId = askLong("Hasta ID: ");
        long doctorId = askLong("Doktor ID: ");
        LocalDateTime dt = askDateTime("Tarih-Saat (yyyy-MM-dd HH:mm): ");
        String note = ConsoleIO.ask("Not (opsiyonel): ");

        if (!doctorRepo.isAvailable(doctorId).orElse(true)) {
            System.out.println("Uyarı: Doktor AVAILABLE=false — işlem iptal.");
            return;
        }
        if (isDoctorSlotConflict(doctorId, dt)) {
            System.out.println("Uyarı: Seçilen tarih-saat dolu — işlem iptal.");
            return;
        }

        try {
            long id = appts.create(new AppointmentCreateRequest(patientId, doctorId, dt, note));
            System.out.println("Randevu oluşturuldu. ID=" + id);
        } catch (IllegalArgumentException ex) {
            System.out.println("İşlem iptal: " + ex.getMessage());
        }
    }

    private void listAppointmentsFlow() throws SQLException {
        System.out.println("\n=== Randevu Listele ===");
        String docStr = ConsoleIO.ask("Doktor ID (boş geçilebilir): ").trim();
        String patStr = ConsoleIO.ask("Hasta ID (boş geçilebilir): ").trim();
        String stStr  = ConsoleIO.ask("Durum (PENDING/CONFIRMED/CANCELLED) (boş geçilebilir): ").trim().toUpperCase();

        Long doctorId = docStr.isEmpty() ? null : Long.parseLong(docStr);
        Long patientId = patStr.isEmpty() ? null : Long.parseLong(patStr);
        AppointmentStatus status = stStr.isEmpty() ? null : AppointmentStatus.valueOf(stStr);

        List<Appointment> list = appts.list(new AppointmentListFilter(doctorId, patientId, status));
        if (list.isEmpty()) {
            System.out.println("Kayıt bulunamadı.");
        } else {
            list.forEach(a -> System.out.printf(
                    "ID=%d | PATIENT=%d | DOCTOR=%d | %s | %s | NOTE=%s | v%d%n",
                    a.getId(), a.getPatientId(), a.getDoctorId(),
                    a.getDateTime().format(DT), a.getStatus().name(),
                    a.getNote(), a.getVersion()
            ));
        }
    }

    private void updateStatusFlow() throws SQLException {
        System.out.println("\n=== Randevu Durum Güncelle ===");
        long id = askLong("Randevu ID: ");
        String stStr = ConsoleIO.ask("Yeni Durum (PENDING/CONFIRMED/CANCELLED): ").trim().toUpperCase();
        AppointmentStatus st;
        try { st = AppointmentStatus.valueOf(stStr); }
        catch (IllegalArgumentException e) { System.out.println("Geçersiz durum."); return; }
        boolean ok = appts.updateStatus(new AppointmentStatusUpdateRequest(id, st));
        System.out.println(ok ? "Durum güncellendi." : "Güncelleme başarısız: ID bulunamadı.");
    }

    private void deleteAppointmentFlow() throws SQLException {
        System.out.println("\n=== Randevu Sil (Hard Delete) ===");
        long id = askLong("Randevu ID: ");
        boolean ok = appts.deleteHard(id);
        System.out.println(ok ? "Silindi." : "Silme başarısız: ID bulunamadı.");
    }

    private void checkDoctorAvailabilityFlow() throws SQLException {
        System.out.println("\n=== Doktor Müsaitliği Kontrol ===");
        long doctorId = askLong("Doktor ID: ");
        LocalDateTime dt = askDateTime("Tarih-Saat (yyyy-MM-dd HH:mm): ");

        boolean availFlag = doctorRepo.isAvailable(doctorId).orElse(true);
        boolean conflict  = isDoctorSlotConflict(doctorId, dt);

        if (!availFlag) { System.out.println("Sonuç: AVAILABLE=false → Müsait değil."); return; }
        if (conflict)   { System.out.println("Sonuç: Slot dolu → Müsait değil.");       return; }
        System.out.println("Sonuç: Müsait.");
    }

    private void whoAmI() {
        var sess = SessionContext.current();
        if (sess.isEmpty()) { System.out.println("Oturum yok."); return; }
        var u = sess.get();
        System.out.printf("ID=%d | USERNAME=%s | ROLE=%s%n", u.id(), u.username(), u.role().name());
    }

    // ---------- Yardımcılar ----------
    private String askUsername() {
        while (true) {
            String u = ConsoleIO.ask("Kullanıcı adı (3-32, harf/rakam/._-): ").trim();
            if (Validators.username(u)) return u;
            System.out.println("Geçersiz kullanıcı adı.");
        }
    }

    private String askEmail() {
        while (true) {
            String e = ConsoleIO.ask("E-posta: ").trim();
            if (Validators.email(e)) return e;
            System.out.println("Geçersiz e-posta.");
        }
    }

    private String askStrongPasswordTwice() {
        System.out.println("""
            Parola Kuralları:
             - En az 6 karakter
             - En az 1 harf ve 1 rakam içermeli
            """);
        while (true) {
            String p1 = ConsoleIO.askPasswordMasked("Parola: ");
            if (!Validators.strongPassword(p1)) { System.out.println("Parola zayıf."); continue; }
            String p2 = ConsoleIO.askPasswordMasked("Parola (tekrar): ");
            if (!p1.equals(p2)) { System.out.println("Parolalar uyuşmuyor."); continue; }
            return p1;
        }
    }

    private long askLong(String prompt) {
        try { return Long.parseLong(ConsoleIO.ask(prompt).trim()); }
        catch (NumberFormatException e) { System.out.println("Geçersiz sayısal değer."); return askLong(prompt); }
    }

    private LocalDateTime askDateTime(String prompt) {
        while (true) {
            String s = ConsoleIO.ask(prompt).trim();
            try {
                LocalDateTime dt = LocalDateTime.parse(s, DT);
                if (dt.isBefore(LocalDateTime.now())) { System.out.println("Geçmiş tarih-saat olamaz."); continue; }
                return dt.withSecond(0).withNano(0);
            } catch (DateTimeParseException e) {
                System.out.println("Format hatalı. Örn: 2025-10-15 14:30");
            }
        }
    }

    private boolean isDoctorSlotConflict(long doctorId, LocalDateTime dt) throws SQLException {
        List<Appointment> list = appts.list(new AppointmentListFilter(doctorId, null, null));
        return list.stream().anyMatch(a -> a.getDateTime().withSecond(0).withNano(0)
                .equals(dt.withSecond(0).withNano(0)));
    }
}
