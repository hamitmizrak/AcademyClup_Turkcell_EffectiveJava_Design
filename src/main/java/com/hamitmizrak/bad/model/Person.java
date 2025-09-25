package com.hamitmizrak.bad.model;

import com.hamitmizrak.bad.common.Audited;

import java.util.Date;

//@Audited("entity") // ❌ KÖTÜ: hiçbir etkisi yok
public class Person {

    // Field
    public Long id;               // ❌ KÖTÜ: public alan
    public String name;           // ❌ KÖTÜ: null kontrolleri yok
    public String nationalId;     // ❌ KÖTÜ: regex/format
    public Date birthDate;        // ❌ KÖTÜ: java.time yerine eski

    // Paramtresiz Cosntructor
    public Person() {}  // ❌ KÖTÜ: anlamsız boş ctor

    // ❌ KÖTÜ: Getter,Setter,toString

}
