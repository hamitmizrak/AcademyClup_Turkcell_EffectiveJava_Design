package com.hamitmizrak.good.runner;

import com.hamitmizrak.good.config.Migrations;
import com.hamitmizrak.good.menu.MainConsole;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Uygulama açıldığında konsol akışını başlatır:
 * - Kullanıcı yoksa: Register → sonra Login
 * - Kullanıcı varsa: Login
 * - Login sonrası: Menü
 */
@Component
@Order(2)
public class ConsoleStarter implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        try {
            Migrations.migrate();
            //new MainConsole();
        } catch (Throwable t) {
            System.err.println("[ConsoleStarter] Konsol akışı beklenmedik biçimde sonlandı: " + t.getMessage());
            t.printStackTrace(System.err);
        }
    }
}