package com.hamitmizrak.good.config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/*
 CLEAN-UP ÖZETİ
 ✅ Template Method benzeri akış: migrate() -> tablo kurulumları.
 ✅ Anlamlı constraint’ler, UNIQUE alanlar, foreign key’ler.
 ✅ Text block ile okunabilir SQL, TIMESTAMP alanları ve VERSION sütunu.
 ✅ İleride Login/Register/WhoAmI için USERS ve ROLE kavramı hazır.
*/
public final class Migrations {

    // parametresiz ctor
    private Migrations() { }

    /** Uygulama başında tablo şemasını hazırlar (idempotent). */
    public static void migrate() throws SQLException {
        // ✅ CLEAN CODE: try-with-resources
        try (Connection connection = DB.getConnection();
             //Statement statement = connection.createStatement()
             Statement preparedStatement = connection.createStatement()
        ) {
            // USERS (Login/Register/WhoAmI için)
            preparedStatement.execute("""
                CREATE TABLE IF NOT EXISTS USERS(
                  ID IDENTITY PRIMARY KEY,
                  USERNAME VARCHAR(64) UNIQUE NOT NULL,
                  EMAIL VARCHAR(128) UNIQUE NOT NULL,
                  PASSWORD_HASH VARCHAR(256) NOT NULL,
                  ROLE VARCHAR(16) NOT NULL,
                  ENABLED BOOLEAN DEFAULT TRUE,
                  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  UPDATED_AT TIMESTAMP
                );
            """);

            // DEPARTMENTS
            preparedStatement.execute("""
                CREATE TABLE IF NOT EXISTS DEPARTMENTS(
                  ID IDENTITY PRIMARY KEY,
                  NAME VARCHAR(64) UNIQUE NOT NULL
                );
            """);

            // DOCTORS
            preparedStatement.execute("""
                CREATE TABLE IF NOT EXISTS DOCTORS(
                  ID IDENTITY PRIMARY KEY,
                  NAME VARCHAR(128) NOT NULL,
                  DEPARTMENT_ID BIGINT,
                  AVAILABLE BOOLEAN DEFAULT TRUE,
                  CONSTRAINT FK_DOC_DEPT FOREIGN KEY (DEPARTMENT_ID) REFERENCES DEPARTMENTS(ID)
                );
            """);

            // PATIENTS
            preparedStatement.execute("""
                CREATE TABLE IF NOT EXISTS PATIENTS(
                  ID IDENTITY PRIMARY KEY,
                  NAME VARCHAR(128) NOT NULL,
                  NATIONAL_ID VARCHAR(16) UNIQUE NOT NULL,
                  PHONE VARCHAR(32),
                  EMAIL VARCHAR(128)
                );
            """);

            // APPOINTMENTS
            preparedStatement.execute("""
                CREATE TABLE IF NOT EXISTS APPOINTMENTS(
                  ID IDENTITY PRIMARY KEY,
                  PATIENT_ID BIGINT NOT NULL,
                  DOCTOR_ID BIGINT NOT NULL,
                  DATETIME TIMESTAMP NOT NULL,
                  STATUS VARCHAR(16) NOT NULL,
                  NOTE VARCHAR(512),
                  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  UPDATED_AT TIMESTAMP,
                  VERSION INT DEFAULT 0,
                  CONSTRAINT FK_APPT_PAT FOREIGN KEY (PATIENT_ID) REFERENCES PATIENTS(ID),
                  CONSTRAINT FK_APPT_DOC FOREIGN KEY (DOCTOR_ID) REFERENCES DOCTORS(ID)
                );
            """);
        }
    }
}
