package com.hamitmizrak.good.runner;

/*
 CLEAN-UP ÖZETİ
 ✅ Uygulama ayağa kalkınca H2 şemasını kurar (idempotent).
 ✅ Test/Boot ayrımını ortadan kaldırır; konsolla aynı DB’ye baksanız da tablolar hazır olur.
*/

import com.hamitmizrak.good.config.Migrations;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class BootMigrations implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Migrations.migrate();
    }
}