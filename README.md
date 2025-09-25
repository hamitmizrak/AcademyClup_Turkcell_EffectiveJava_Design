# Hospital Appointment System — Clean Architecture (Java 17 + H2 + JDBC)

> **Amaç:** Basit görünen bir **Hastane Randevu Sistemi**ni, önce *kötü kod* (anti‑pattern’li) bir sürümden başlatıp, ardından **Clean Code + SOLID + KISS + YAGNI** ve uygun **Design Pattern**’lerle **üretim kalitesine** taşımak. Bu repo, eğitim odaklı olarak tam bir dönüşümü ve mimari örüntüleri göstermeyi hedefler.

---

## İçindekiler

* [Özellikler (Ne yapar?)](#özellikler-ne-yapar)
* [Mimari Genel Bakış](#mimari-genel-bakış)
* [Proje Ağacı](#proje-ağacı)
* [Veritabanı Şeması (H2)](#veritabanı-şeması-h2)
* [RBAC (Rol Tabanlı Yetki) Haritası](#rbac-rol-tabanlı-yetki-haritası)
* [DTO’lar ve Örnek Payload’lar](#dtolar-ve-örnek-payloadlar)
* [HTTP Adapter — Uç Noktalar](#http-adapter--uç-noktalar)
* [Doğrulama (Regex/Strategy/Chain)](#doğrulama-regexstrategychain)
* [Güvenlik (Parola/Token/Proxy)](#güvenlik-parolatokenproxy)
* [Eşzamanlılık (Executors/Optimistic Lock)](#eşzamanlılık-executorsoptimistic-lock)
* [Olaylar (Observer/EventBus)](#olaylar-observereventbus)
* [Serileştirme & Aktarım (JSON)](#serileştirme--aktarım-json)
* [Günlükleme (Logging) ve I18n](#günlükleme-logging-ve-i18n)
* [Performans & İndeksleme](#performans--indeksleme)
* [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
* [Testler](#testler)
* [Kötü Kod → İyi Kod Dönüşümü](#kötü-kod--iyi-kod-dönüşümü)
* [Tasarım Desenleri (Nerede ve Neden?)](#tasarım-desenleri-nerede-ve-neden)
* [Yapılandırma (application.properties)](#yapılandırma-applicationproperties)
* [Sorun Giderme (FAQ)](#sorun-giderme-faq)
* [Yol Haritası](#yol-haritası)
* [Lisans](#lisans)

---

## Özellikler (Ne yapar?)

* **Kayıt/Giriş (Register/Login):** PBKDF2 + SecureRandom salt ile parola hash’i, süreli token yönetimi (`tokens` tablosu).
* **Rol Tabanlı Yetkilendirme (RBAC):** ADMIN/DOCTOR/PATIENT rollerine göre metot erişimleri **Proxy** ile denetlenir.
* **Doktor Müsaitlik (Slots):** Doktorlar zaman dilimi açar; **optimistic lock (version)** ile çifte rezervasyon engellenir.
* **Randevu (Appointments):** Hasta slot seçerek randevu oluşturur; durum yönetimi (PENDING/CONFIRMED/CANCELLED/COMPLETED/NO\_SHOW).
* **Listeleme & Rapor:** Doktorun belirli aralıktaki randevuları (gerekirse `parallelStream`), hastanın geçmişi (sayfalama).
* **Doğrulama:** E-posta regex, TCKN algoritması, telefon normalizasyonu; **Composite + Chain** ile istek doğrulama akışı.
* **Aktarım:** Güvenli **JSON export/import** (Jackson). **Java native serialization kullanılmaz**.
* **Günlükleme & I18n:** `AppLogger` portu → JUL/SLF4J köprüleri; `messages.properties` ile çok dillilik.
* **Arayüzler:** CLI (konsol) + **HTTP Adapter** (hafif REST benzeri uçlar).

---

## Mimari Genel Bakış

**Ports & Adapters (Hexagonal / Clean Architecture)** yaklaşımı:

* **Domain**: Entity/Value Object/Enum, DTO `record`’ları ve Mapper arayüzleri.
* **Ports**: Repository & Service arayüzleri.
* **Application**: Use‑case implementasyonları, Validator (Composite/Chain), Decorator (Logging, Caching), Proxy (RBAC), Observer EventBus.
* **Infrastructure**: H2/JDBC repo’lar, PBKDF2, i18n, logging köprüleri, DI/Assembler, HTTP adapter.
* **Bootstrap**: `Main` (CLI) ve opsiyonel HTTP sunucusu başlatıcıları.

> **SOLID**: S — tek sorumluluk; O — yeni davranışa açık (Decorator/Strategy); L — port sözleşmelerine bağlı; I — ayrık arayüzler (segregation); D — üst katmanlar arayüzlere bağımlı (**DIP**).

---

## Proje Ağacı

```text
hospital-appointment-clean/
├─ pom.xml
├─ src/main/java/com/hospital/clean/
│  ├─ common/                # Result, Page, Validator, Events, @Audited
│  ├─ domain/
│  │  ├─ model/              # User, Doctor, Patient, Department, TimeSlot, enums
│  │  ├─ dto/                # record tabanlı DTO’lar
│  │  ├─ mapper/             # DTO <-> Domain arayüzleri
│  │  └─ ports/
│  │     ├─ repository/      # Repository interfaces (ports)
│  │     └─ service/         # Service interfaces (ports), factories
│  ├─ application/           # Use-case’ler, validator, decorators, proxy, events
│  │  ├─ mapper/             # Mapper implementasyonları
│  │  ├─ validation/         # Composite/Chain & Strategy doğrulayıcılar
│  │  ├─ security/           # Authorization + SecuredProxyFactory (RBAC)
│  │  ├─ service/            # Auth/Appointment/Availability/Doctor/Patient servisleri
│  │  ├─ decorators/         # Logging & Caching decorator’ları
│  │  ├─ memento/            # SlotMemento örneği
│  │  └─ prototype/          # AppointmentTemplate + TemplateRegistry
│  ├─ infrastructure/
│  │  ├─ persistence/        # H2 DataSource, Migration, Tx template
│  │  │  └─ h2/              # *RepositoryH2 implementasyonları
│  │  ├─ events/             # EventBusSimple
│  │  ├─ concurrency/        # ExecutorProviderImpl (IO/CPU) + shutdown
│  │  ├─ logging/            # JUL & SLF4J adapter’ları
│  │  ├─ i18n/               # MessageBundleImpl
│  │  ├─ factory/            # RepositoryFactoryH2
│  │  ├─ di/                 # ServiceAssembler (Composition Root)
│  │  ├─ http/               # HttpServerAdapter (hafif REST)
│  │  └─ exportjson/         # SafeJsonExport
│  └─ bootstrap/Main.java    # CLI
└─ src/main/resources/
   ├─ db/migration/V1__init.sql
   ├─ db/migration/V2__performance.sql
   ├─ application.properties
   └─ messages.properties
```

---

## Veritabanı Şeması (H2)

**Temel tablolar:**

* `users(id, username, password_hash, salt, full_name, national_id, email, phone, created_at, updated_at)`
* `roles(id, name)` + `user_roles(user_id, role_id)`
* `departments(id, code UNIQUE, name)` *(Flyweight cache ile bellekte paylaştırılır)*
* `doctors(id=users.id, department_id, license_no UNIQUE, bio, is_active)`
* `patients(id=users.id, insurance_no, notes)`
* `slots(id, doctor_id, start_ts, end_ts, is_booked, version)` *(optimistic lock: `version`)*
* `appointments(id, patient_id, doctor_id, slot_id?, start_ts, end_ts, status, notes, created_at, updated_at)`
* `tokens(id, user_id, token UNIQUE, expires_at, created_at)` *(login oturumları)*

**İndeksler (V2):**

* `tokens(expires_at)`
* `appointments(status, start_ts)`
* `slots(doctor_id, is_booked, start_ts)`

---

## RBAC (Rol Tabanlı Yetki) Haritası

**SecuredProxyFactory** ile method → roller eşlemesi:

| Metot                               | İzinli Roller          |
| ----------------------------------- | ---------------------- |
| `AppointmentService.create`         | PATIENT, ADMIN         |
| `AppointmentService.get`            | PATIENT, DOCTOR, ADMIN |
| `AppointmentService.findForDoctor`  | DOCTOR, ADMIN          |
| `AppointmentService.findForPatient` | PATIENT, ADMIN         |
| `AppointmentService.changeStatus`   | DOCTOR, ADMIN          |
| `AppointmentService.cancel`         | PATIENT, ADMIN         |

> Proxy ihlalde `SecurityException("forbidden:<roles>")` fırlatır.

---

## DTO’lar ve Örnek Payload’lar

**Register:**

```json
{
  "username": "ali",
  "rawPassword": "S3cr3t!",
  "fullName": "Ali Veli",
  "nationalId": "10000000146",
  "email": "ali@example.com",
  "phone": "+905551112233"
}
```

**Login:**

```json
{ "username": "ali", "rawPassword": "S3cr3t!" }
```

**AppointmentCreateRequest:**

```json
{
  "patientId": 1,
  "doctorId": 1001,
  "slotId": 42,
  "start": "2025-10-01T09:00:00",
  "end":   "2025-10-01T09:30:00",
  "notes": "Kontrol"
}
```

---

## HTTP Adapter — Uç Noktalar

Yerleşik `com.sun.net.httpserver.HttpServer` ile hafif uçlar. Body: `x-www-form-urlencoded`.

| Method | Path                      | Parametreler                                          | Açıklama                                  |
| ------ | ------------------------- | ----------------------------------------------------- | ----------------------------------------- |
| `POST` | `/api/register`           | `username,password,fullName,nationalId,email,phone`   | Kayıt; dönen: `userId`                    |
| `POST` | `/api/login`              | `username,password`                                   | Giriş; dönen: `token`                     |
| `POST` | `/api/slot/add`           | `doctorId,start,end,token` (`yyyy-MM-dd HH:mm`)       | Slot ekler; dönen: `slotId`               |
| `POST` | `/api/appointment/create` | `patientId,doctorId,slotId?,start?,end?,notes?,token` | Randevu oluşturur; dönen: `appointmentId` |

**Örnek:**

```bash
curl -XPOST localhost:8080/api/register \
  -d "username=ali&password=123&fullName=Ali Veli&nationalId=10000000146&email=ali@example.com&phone=05551112233"

curl -XPOST localhost:8080/api/login -d "username=ali&password=123"
# {"token":"..."}

curl -XPOST localhost:8080/api/slot/add \
  -d "doctorId=1001&start=2025-10-01 09:00&end=2025-10-01 09:30&token=..."

curl -XPOST localhost:8080/api/appointment/create \
  -d "patientId=1&doctorId=1001&slotId=42&token=..."
```

---

## Doğrulama (Regex/Strategy/Chain)

* **E‑posta**: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,63}$` + domain denetimi (kara/beyaz liste simülasyonu)
* **TCKN**: 11 hane; `d10 = ((odd*7)-even)%10`, `d11 = sum(d1..d10)%10`
* **Telefon**: `+90XXXXXXXXXX` formatına normalize (başta `0`/`90` varyasyonları)
* **Randevu İsteği**: Composite/Chain ile `id.missing`, `date.missing`, `date.order`, `duration.invalid` kontrolleri

Hata mesajları **i18n** anahtarları üzerinden yönetilir (örn. `email.invalid`).

---

## Güvenlik (Parola/Token/Proxy)

* **Parola Hash**: PBKDF2WithHmacSHA256, `ITER≈120k`, `KEYLEN=256`, **SecureRandom 32‑byte salt**
* **Token**: `Base64Url(32 byte)` → `tokens(token, expires_at)` tablosunda saklanır
* **RBAC**: `SecuredProxyFactory` ile method bazlı kural uygulama
* **SQL Güvenliği**: **PreparedStatement** + try‑with‑resources (Injection önlenir)
* **Serialization**: **Java native serialization yok** → yalnız **JSON** (Jackson)

> Not: Prod için ek tedbirler (rate limiting, audit log, kuyruk bazlı bildirim, HTTPS) önerilir.

---

## Eşzamanlılık (Executors/Optimistic Lock)

* **ExecutorProvider**: Ayrı **IO** ve **CPU** havuzları, **graceful shutdown**
* **CompletableFuture**: Olay yayınlama gibi arka plan işler
* **Optimistic Lock**: `slots.version` ile koşullu güncelleme (`WHERE id=? AND version=?`)

---

## Olaylar (Observer/EventBus)

* **AppointmentCreated(appointmentId, patientId, doctorId)** olayı yayınlanır.
* `EventBusSimple` aboneleri asenkron tetiklenir (ör. bildirim/log).

---

## Serileştirme & Aktarım (JSON)

* **SafeJsonExport**: `AppointmentService.findForPatient` sonucunu **güvenli JSON** olarak dışa aktarır.
* **Import**: JSON geri okunur (bilgilendirme/rapor amaçlı; DB’ye doğrudan yazmaz).

---

## Günlükleme (Logging) ve I18n

* `AppLogger` portu → **JUL** veya **SLF4J** adapter’ları (konfigürasyonla seçilebilir)
* `messages.properties` → i18n; `MessageBundle` portu üzerinden erişim
* `LoggingAppointmentService` (Decorator) ile servis seviyesinde iz bırakma

---

## Performans & İndeksleme

* **İndeksler**: `tokens(expires_at)`, `appointments(status,start_ts)`, `slots(doctor_id,is_booked,start_ts)`
* **Keyset Pagination** önerilir (büyük `OFFSET` yerine `WHERE start_ts > ?`)
* Kolon üzerinde fonksiyon kullanmaktan kaçının (`WHERE DATE(start_ts)=...` **kötü**)
* Sadece gerekli yerlerde `parallelStream` kullanın

---

## Kurulum ve Çalıştırma

### Gereksinimler

* **Java 17+**, **Maven 3.8+**

### Derleme

```bash
mvn -q -DskipTests package
```

### CLI Başlatma

```bash
java -cp target/hospital-appointment-clean-1.0.0.jar com.hospital.clean.bootstrap.Main
```

### HTTP Adapter (opsiyonel)

`Main` yerine `HttpServerAdapter`’ı başlatmak için (örnek psödokod):

```java
var assembler = new ServiceAssembler();
var http = new HttpServerAdapter(assembler);
http.start(8080);
```

### H2 Konumu

* Varsayılan URL: `jdbc:h2:~/hospital_clean_db;AUTO_SERVER=TRUE` (kullanıcı home dizininde dosya)

---

## Testler

### Çalıştırma

```bash
mvn -q test
```

### İçerik

* `AppointmentServiceTest`: create → get → status change uçtan uca
* `SecurityPasswordHasherTest`: PBKDF2 hash/verify doğrulaması

---

## Kötü Kod → İyi Kod Dönüşümü

| Konu              | Kötü Sürüm                           | İyi Sürüm                                   |
| ----------------- | ------------------------------------ | ------------------------------------------- |
| Mimari            | God Class, tight coupling            | Ports & Adapters, DIP, DI/Assembler         |
| Veri Erişimi      | String birleştirme, kaynak sızıntısı | PreparedStatement + try‑with‑resources      |
| Parola            | Düz metin/zayıf hash                 | PBKDF2 + SecureRandom salt + token expiry   |
| Slot Rezervasyonu | Race condition                       | Optimistic lock (version)                   |
| Doğrulama         | Dağınık if/else                      | Strategy + Composite/Chain + i18n           |
| Logging           | `System.out`                         | AppLogger + Decorator + SLF4J/JUL köprüsü   |
| Aktarım           | Java serialization                   | JSON (Jackson)                              |
| Concurrency       | Raw thread, kapanış yok              | ExecutorProvider + graceful shutdown        |
| i18n              | Sabit string                         | ResourceBundle                              |
| Test              | Zor, kırılgan                        | Port’lar sayesinde kolay test               |
| Pattern Matching  | if‑else yığını                       | `switch` expressions + `instanceof` pattern |

---

## Tasarım Desenleri (Nerede ve Neden?)

* **Builder**: `User`, `Appointment` oluşturma
* **Factory Method / Abstract Factory**: `RepositoryFactoryH2`, `ServiceFactory`
* **Singleton**: `DataSourceProvider`, logger/i18n sağlayıcıları
* **Prototype**: `AppointmentTemplate` (rutin randevu kalıbı)
* **Dependency Injection (DIP)**: `ServiceAssembler` (Composition Root)
* **Adapter/Bridge**: Mapper’lar & Logging (JUL/SLF4J), HTTP adapter
* **Composite + Chain of Responsibility**: Doğrulama zinciri
* **Decorator**: `LoggingAppointmentService`, `CachingDoctorService`
* **Facade**: `AdminFacadeImpl` (seed)
* **Flyweight**: `Department` metadata cache
* **Proxy**: `SecuredProxyFactory` (RBAC)
* **Observer**: `EventBusSimple` + `AppointmentCreated`
* **Memento**: `SlotMemento` (geri alma örneği)
* **Template Method**: `JdbcTxTemplate` (transaction kalıbı)

---

## Yapılandırma (application.properties)

```properties
# H2 JDBC
db.url=jdbc:h2:~/hospital_clean_db;AUTO_SERVER=TRUE
db.user=sa
db.pass=

# i18n
a pp.locale=tr_TR

# logger seçimi: jul | slf4j
app.logger=jul

# java.util.logging örnek handler
handlers= java.util.logging.ConsoleHandler
.level= INFO
java.util.logging.ConsoleHandler.level = INFO
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
```

> SLF4J için `app.logger=slf4j` seçeneğini kullanın (pom’da `slf4j-simple` mevcut).

---

## Sorun Giderme (FAQ)

* **“Table not found” / migration çalışmadı** → `MigrationRunner.runAll()` bootstrap’te çağrılıyor; resource yolu doğru mu?
* **H2 dosyası kilitlendi** → Aynı DB’ye iki süreç bağlanıyorsa `AUTO_SERVER=TRUE` kullanın veya kapalıyken tekrar deneyin.
* **RBAC hatası (`forbidden`)** → Token sahip olduğu rol, çağırılan metoda uygun değil.
* **Saat/Tarih parse hatası** → HTTP adapter `yyyy-MM-dd HH:mm` bekler (CLI farklı değil).

---

## Yol Haritası

* Rate limiting, audit log (@Audited) için AOP benzeri proxy
* Postgres profili (Testcontainers) ve RepositoryFactory genişletmesi
* Keyset pagination implementasyonu
* Daha zengin HTTP API (OpenAPI şeması), DTO doğrulama hata gövdeleri

---

## Lisans

MIT (örn.) — Proje eğitim amaçlıdır; gerçek üretim sistemine geçmeden önce ek güvenlik ve sağlam testler önerilir.
