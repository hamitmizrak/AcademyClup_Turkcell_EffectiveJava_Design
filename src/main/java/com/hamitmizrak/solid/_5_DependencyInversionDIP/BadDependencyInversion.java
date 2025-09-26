package com.hamitmizrak.solid._5_DependencyInversionDIP;


// ❌ KÖTÜ: Yüksek seviye akış somut sınıfa kilitli → test/alternatif zor.
class EmailNotifier {
    void send(String to, String message) {
        System.out.println("mail " + to + ": " + message);
    }
}

// MAIN CLASS
public class BadDependencyInversion {

    void register(String email) {
        new EmailNotifier().send(email, "Welcome");
    } // somuta bağımlı

    public static void main(String[] args) {
        new BadDependencyInversion().register("a@b.com");
    }
}
