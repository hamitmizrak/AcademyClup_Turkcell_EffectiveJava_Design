package com.hamitmizrak.bad.model;


/*
 ❌ KÖTÜ:Neden kötü? role gibi alan yok; specialization için Department varken String kullanımı, public alan.
 */
public class ModelPatient extends ModelPerson {

    public String phone;  // ❌ KÖTÜ: Regex yok, public alan
    public String email;  // ❌ KÖTÜ: format kontrolü, public alan
    public String insurance; // ❌ KÖTÜ: Sabit değerler, enum yoktur, public alan
}
