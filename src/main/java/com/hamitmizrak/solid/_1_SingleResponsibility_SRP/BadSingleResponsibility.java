package com.hamitmizrak.solid._1_SingleResponsibility_SRP;

// ❌ KÖTÜ: Aynı sınıf hem hesaplıyor, hem yazdırıyor, hem "DB'ye kaydediyor".
public class BadSingleResponsibility {

    public static void p(double p, double t){
        // 1- Dikkat Hesaplama iş kuramı
        double total = p+p*t;  // (1) İş kuralı burada

        // 2- Konsolea yadırmak
        System.out.println("Total: "+total);  // (2) Sunum/IO burada

        // 3- Kalıcık: Veri tabanı
        // savetoDb(total);
        System.out.println("Database saved: "+total);   // (3) Kalıcılık burada

    }

    public static void main(String[] args) {
         BadSingleResponsibility.p(12,20);  // psvm: kullanım
    }
}
