package com.hamitmizrak.bad;


import com.hamitmizrak.bad.auth.Role;
import com.hamitmizrak.bad.auth.User;
import com.hamitmizrak.bad.concurrency.ConcurrencyDemo;
import com.hamitmizrak.bad.repository.AppointmentRepository;
import com.hamitmizrak.bad.repository.DoctorRepository;
import com.hamitmizrak.bad.service.AuthService;
import com.hamitmizrak.bad.service.HospitalService;
import com.hamitmizrak.bad.util.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class HospitalAppMain {

    static Scanner sc = new Scanner(System.in);
    static SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static String TOKEN = null; // Kötü: global

    public void allData(){
        //Server.createWebServer("-web", "-webPort", "4444", "-ifExists").start();

        System.out.println("Hospital BAD App v0.0.2 (eklemeler)");
        AppointmentRepository.init();
        DoctorRepository.init();
        AuthService.init();

        while (true) {
            System.out.println("""
                1) Randevu Ekle
                2) Listele
                3) Durum Güncelle
                4) Sil
                5) URL'yi Reflection ile değiştir (KÖTÜ)
                6) Register (KÖTÜ)
                7) Login (KÖTÜ)
                8) Ben Kimim (KÖTÜ)
                9) Export (Serialization KÖTÜ)
               10) Import (Serialization KÖTÜ)
               11) Concurrency Demo (KÖTÜ)
               12) ForkJoin Fib (KÖTÜ)
               13) Switch Demo (KÖTÜ)
               14) Doktor Müsaitlik Değiştir (KÖTÜ)
               15) Regex Demo (KÖTÜ)
                0) Çık
               \s""");
            System.out.print("Seçim: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> add();
                case "2" -> list();
                case "3" -> update();
                case "4" -> delete();
                case "5" -> DangerousReflection.mutateDbUrl();
                case "6" -> register();
                case "7" -> login();
                case "8" -> whoami();
                case "9" -> ExportUtil.exportAppointments();
                case "10" -> ExportUtil.importAppointments();
                case "11" -> ConcurrencyDemo.runExecutorMess();
                case "12" -> forkJoin();
                case "13" -> switchDemo();
                case "14" -> toggleDoctor();
                case "15" -> regexDemo();
                case "0" -> { System.out.println("Bye"); return; }
                default -> System.out.println("Hatalı seçim");
            }
        }
    }

    public static void main(String[] args) throws SQLException {

    }

    static void add() {
        try {
            System.out.print("Hasta Id: "); Long pid = Long.valueOf(sc.nextLine());
            System.out.print("Doktor Id: "); Long did = Long.valueOf(sc.nextLine());
            System.out.print("Başlangıç (yyyy-MM-dd HH:mm): "); Date start = F.parse(sc.nextLine());
            System.out.print("Süre(dk): "); int m = Integer.parseInt(sc.nextLine());
            Long id = HospitalService.createAppointment(pid, did, start, m);
            BadLogger.log("created id=" + id);
        } catch (NumberFormatException | ParseException e) {
            System.out.println("Hata.");
        }
    }

    static void list() {
        HospitalService.list().forEach(a ->
                System.out.println(a.id + " P:" + a.patientId + " D:" + a.doctorId + " " + a.start + " " + a.minutes + " " + a.status)
        );
    }

    static void update() {
        System.out.print("Id: "); Long id = Long.valueOf(sc.nextLine());
        System.out.print("Yeni Durum(PENDING/CONFIRMED/CANCELLED/COMPLETED/NO_SHOW): ");
        String s = sc.nextLine().trim();
        HospitalService.changeStatus(id, s);
    }

    static void delete() {
        System.out.print("Id: "); Long id = Long.valueOf(sc.nextLine());
        HospitalService.remove(id);
    }

    static void register() {
        User u = new User();
        System.out.print("Username: "); u.username = sc.nextLine();
        System.out.print("Password: "); u.password = sc.nextLine();
        System.out.print("Ad Soyad: "); u.name = sc.nextLine();
        System.out.print("TC: "); u.nationalId = sc.nextLine();
        System.out.print("Rol(ADMIN/DOCTOR/PATIENT): ");
        try { u.role = Role.valueOf(sc.nextLine().trim()); } catch (Exception ignored) {}
        Long id = AuthService.register(u);
        System.out.println("Kayıt id=" + id);
    }

    static void login() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        TOKEN = AuthService.login(u, p);
        System.out.println("Token=" + TOKEN);
    }

    static void whoami() {
        var me = AuthService.whoAmI(TOKEN);
        System.out.println(me == null ? "Yok" : (me.username + " / " + me.role));
    }

    static void forkJoin() {
        System.out.print("n: ");
        int n = Integer.parseInt(sc.nextLine());
       ConcurrencyDemo.runForkJoinBad(n);
    }

    static void switchDemo() {
        SwitchDemo.printAny("merhaba");
        SwitchDemo.printAny(41);
        SwitchDemo.printAny(null); // Kötü: null → NPE olabilir
    }

    static void toggleDoctor() {
        System.out.print("DoctorId: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Spec: ");
        String spec = sc.nextLine();
        System.out.print("Available true/false: ");
        boolean av = Boolean.parseBoolean(sc.nextLine());
        int r = DoctorRepository.upsertDoctor(id, name, spec, av);
        System.out.println("Sonuç=" + r);
    }

    static void regexDemo() {
        System.out.print("Email: ");
        String e = sc.nextLine();
        System.out.println("Email valid? " + Validators.isEmail(e));
        System.out.print("TC: ");
        String t = sc.nextLine();
        System.out.println("TC valid? " + Validators.isNationalId(t));
        System.out.print("Telefon: ");
        String ph = sc.nextLine();
        System.out.println("Telefon valid? " + Validators.isPhone(ph));
    }
}
