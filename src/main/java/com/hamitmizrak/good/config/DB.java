package com.hamitmizrak.good.config;

/*
  ──────────────────────────────────────────────────────────────────────────────
  DB.java — Singleton (Initialization-on-Demand Holder Idiom) Uygulaması
  ──────────────────────────────────────────────────────────────────────────────
  Neden Singleton?
  - DB erişim katmanı tek bir global yapılandırmaya (URL, USER, PASS) sahip olsun.
  - Nesne ömrü ve init sırası kontrol altında, thread-safe ve lazy olsun.
  - Gereksiz kilitlenme/senkronizasyon maliyeti olmasın.

  Neden tek bir Connection saklamıyoruz?
  - Tek Connection paylaşımı yarış koşullarına, "connection already closed" ve
    auto-commit/transaction sorunlarına yol açar.
  - Doğru yaklaşım: Singleton sadece "fabrika/konfigürasyon"u tekilleştirir;
    her talepte yeni Connection oluşturulur. (İleride: HikariCP pool önerilir.)

  URL çözümleme stratejisi (öncelik sırası):
    1) -Dapp.db.url (JVM arg ile override)
    2) -Dspring.datasource.url (Spring ile uyum)
    3) Sabit fallback (H2 MEM, AUTO_SERVER kaldırıldı; talebiniz doğrultusunda)
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class DB {

    // ── [SINGLETON] Holder idiomu: lazy, thread-safe, sync maliyeti yok ───────
    private static final class Holder {
        private static final DB INSTANCE = new DB();
    }

    /** [SINGLETON] Global tek örneğe erişim noktası */
    public static DB getInstance() {
        return Holder.INSTANCE;
    }

    /** [SINGLETON] Dışarıdan new'lenmeyi engelle */
    private DB() { /* init gerekiyorsa buraya koyun */ }

    // ── Yapılandırma (tekilleştirilmiş durum) ──────────────────────────────────
    // URL, test/override senaryoları için değiştirilebilir tutuluyor.
    private static final AtomicReference<String> URL = new AtomicReference<>(defaultUrl());

    // Not: USER/PASS tipik olarak deployment başında belirlenir; runtime değiştirme
    // ihtiyacı nadirdir. Gerekirse benzer AtomicReference ile yönetilebilir.
    private static final String USER = defaultUser();
    private static final String PASS = defaultPass();

    // ── Varsayılanları çözümleme (sıra önemlidir) ──────────────────────────────
    private static String defaultUrl() {
        String cli = System.getProperty("app.db.url");
        if (cli != null && !cli.isBlank()) return cli;

        String spring = System.getProperty("spring.datasource.url");
        if (spring != null && !spring.isBlank()) return spring;

        // Fallback: H2 bellek içi; AUTO_SERVER istenildiği üzere kaldırıldı.
        // DB_CLOSE_DELAY=-1 => JVM ayakta kaldıkça veriyi tut.
        return "jdbc:h2:mem:hospital;DB_CLOSE_DELAY=-1";
    }

    private static String defaultUser() {
        // Önce app.db.user, yoksa spring.datasource.username, yoksa "sa"
        return System.getProperty(
                "app.db.user",
                System.getProperty("spring.datasource.username", "sa")
        );
    }

    private static String defaultPass() {
        // Önce app.db.pass, yoksa spring.datasource.password, yoksa boş
        return System.getProperty(
                "app.db.pass",
                System.getProperty("spring.datasource.password", "")
        );
    }

    // ── KAMUSAL API ────────────────────────────────────────────────────────────

    /**
     * Singleton örneği üzerinden bağlantı elde etme.
     * Her çağrıda yeni Connection döner (pool yoksa).
     */
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL.get(), USER, PASS);
    }

    /**
     *  Eski çağrıları kırmamak için statik kısayol.
     * Projede `DB.getConnection()` kullanımları varsa çalışmaya devam eder.
     * Yeni kodda `DB.getInstance().openConnection()` tercih edin.
     */
    public static Connection getConnection() throws SQLException {
        return getInstance().openConnection();
    }

    // ── Test/teşhis yardımcıları ───────────────────────────────────────────────

    /** Eğitim/demo/test override için (reflection/whitebox testte kullanılabilir) */
    static void setUrlUnsafe(String newUrl) {
        URL.set(Objects.requireNonNull(newUrl));
    }

    /** Tanılama/test amaçlı mevcut URL'i döner. */
    public static String currentUrl() {
        return URL.get();
    }
}
