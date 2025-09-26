package com.hamitmizrak.bad.model;

public class ModelDoctor extends ModelPerson {

    public String specialization;  // ❌ KÖTÜ: Regex yok, public alan
    public boolean avaiable;  // ❌ KÖTÜ: format kontrolü, public alan
}
