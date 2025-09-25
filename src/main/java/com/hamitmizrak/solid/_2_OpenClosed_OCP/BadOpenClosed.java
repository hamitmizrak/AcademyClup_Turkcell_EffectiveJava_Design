package com.hamitmizrak.solid._2_OpenClosed_OCP;



// ❌ KÖTÜ: Yeni indirim geldikçe bu sınıfı değiştiriyoruz → OCP ihlali.
public class BadOpenClosed {

    double apply(String type, double base){
        if ("STUDENT".equals(type)) return base * 0.8;
        if ("SGK".equals(type))     return base * 0.9;
        return base;
    }

    public static void main(String[] args) { // psvm: kullanım
        System.out.println(new BadOpenClosed().apply("STUDENT", 300)); // 240
    }
}
