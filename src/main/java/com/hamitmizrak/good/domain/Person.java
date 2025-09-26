package com.hamitmizrak.good.domain;

/*
 CLEAN-UP ÖZETİ
 ✅ Abstraction: Ortak kişi alanları tek yerde.
 ✅ Encapsulation: Alanlar private; getter/setter kontrollü.
 ✅ Gereksiz Serializable kaldırıldı (ihtiyaç olursa explicit eklenir).
*/

import java.time.LocalDate;

public abstract  class Person {
    private Long id;
    private String name;
    private String nationalId;
    private LocalDate birthDate;

    protected Person() { }

    public Long getId()            { return id; }
    public String getName()        { return name; }
    public String getNationalId()  { return nationalId; }
    public LocalDate getBirthDate(){ return birthDate; }

    public void setId(Long id)                     { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setNationalId(String nationalId)   { this.nationalId = nationalId; }
    public void setBirthDate(LocalDate birthDate)  { this.birthDate = birthDate; }
}
