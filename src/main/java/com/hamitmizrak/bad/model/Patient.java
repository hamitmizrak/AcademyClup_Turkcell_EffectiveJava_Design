package com.hamitmizrak.bad.model;

public class Patient extends Person{

    public String phone;  // ❌ KÖTÜ: Regex yok, public alan
    public String email;  // ❌ KÖTÜ: format kontrolü, public alan
    public String insurance; // ❌ KÖTÜ: Sabit değerler, enum yoktur, public alan
}
