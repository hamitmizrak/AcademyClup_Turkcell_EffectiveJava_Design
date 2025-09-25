package com.hamitmizrak.solid._1_SingletonResponsibility;

//
public class BadSingletonResponsibility {

    public static void p(double p, double t){
        // 1- Dikkat Hesaplama iş kuramı
        double total = p+p*t;

        // 2- Konsolea yadırmak
        System.out.println("Total: "+total);

        // 3- Kalıcık: Veri tabanı
        // savetoDb(total);
        System.out.println("Database saved: "+total);

    }

    public static void main(String[] args) {
         BadSingletonResponsibility.p(12,20);
    }
}
