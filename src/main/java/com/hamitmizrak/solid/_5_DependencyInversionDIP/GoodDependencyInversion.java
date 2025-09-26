package com.hamitmizrak.solid._5_DependencyInversionDIP;


// ✅ İYİ: Yüksek seviye (UserService) soyuta bağımlı; ayrıntı (Email) dışarıdan verilir.
interface INotifier {
    void send(String to, String msg);
}

// Email
class Email implements INotifier {
    public void send(String to, String msg) {
        System.out.println("mail " + to + ": " + msg);
    }
}


// Ne? Yüksek seviye modüller somut ayrıntılara değil, soyutlamalara bağlı olmalı.
// Neden? Esneklik/test: 3rd-party/somut sınıfı kolayca değiştir, mock et.
// Ne zaman? “İçeride new Somut() geçiyor, testte değiştiremiyorum” diyorsan DIP uygula.
// Koku: Somut sınıflara sıkı bağlılık, DI yok, testte mock’lama zorluğu.
// Main Class
public class GoodDependencyInversion {

    // 1-final : field değiştirilemez
    // 2-final : metotlarda Override
    // 3-final : classlarda extends
    // 4-final : field kullanırsam beni parametreli constructora zorlar (LOMBOK-DI)

    // FIELD
    private final INotifier iNotifier; // DI (constructor injection)

    // Parametreli constructor
    GoodDependencyInversion(INotifier n) {
        this.iNotifier = n;
    }

    void register(String email) {
        iNotifier.send(email, "Welcome");
    }

    // PSVM
    public static void main(String[] args) {
        GoodDependencyInversion us = new GoodDependencyInversion(new Email());      // bağlama noktası burada
        us.register("a@b.com");
    } // end psvm

} // end GoodDependencyInversion

