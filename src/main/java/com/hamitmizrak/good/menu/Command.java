package com.hamitmizrak.good.menu;

/*
 CLEAN-UP ÖZETİ
 ✅ Command Pattern: Her menü maddesi tek bir komut sınıfı.
 ✅ Test edilebilirlik ve SRP.
*/

public interface Command {
    String label();
    void run() throws Exception;
}
