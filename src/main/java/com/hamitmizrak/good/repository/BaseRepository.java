package com.hamitmizrak.good.repository;

/*
 CLEAN-UP ÖZETİ
 ✅ SRP: Yalnızca bağlantı erişimi sağlar.
 ✅ DIP: Üst sınıftan alınan bağlantı ile alt repo’lar DB’den soyutlanır.
*/

import com.hamitmizrak.good.config.DB;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseRepository {
    protected Connection connection() throws SQLException {
        // yazdığımız DB'deki getConnection çağır.
        return DB.getConnection();
    }
}
