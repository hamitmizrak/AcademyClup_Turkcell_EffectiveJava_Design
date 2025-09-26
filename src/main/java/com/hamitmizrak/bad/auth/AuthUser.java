package com.hamitmizrak.bad.auth;


import com.hamitmizrak.bad.model.ModelPerson;

public class AuthUser extends ModelPerson { // Kötü: domain karışıyor (Person + User)
    public String username;     // Kötü: public alanlar
    public String password;     // Kötü: Base64 "şifreleme"
    public AuthRole role = AuthRole.PATIENT;
    public String token;        // Kötü: in-memory, expiry yok

    // Kötü: Model'de tablo SQL'i tutuluyor (katman ihlali)
    public static final String CREATE_SQL = """
        CREATE TABLE IF NOT EXISTS USERS(
          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
          USERNAME VARCHAR(64) UNIQUE,
          PASSWORD VARCHAR(256),
          ROLE VARCHAR(32),
          NAME VARCHAR(128),
          NATIONAL_ID VARCHAR(32)
        );
        """;
}
