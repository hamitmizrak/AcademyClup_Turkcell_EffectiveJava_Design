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

public class DB {
}
