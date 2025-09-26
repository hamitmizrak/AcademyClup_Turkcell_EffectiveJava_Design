package com.hamitmizrak.good.domain;

/*
 CLEAN-UP ÖZETİ
 ✅ Encapsulation; alan adları/anlamları düzenli.
*/
public class Doctor extends Person {
    private Long departmentId;
    private boolean available = true;

    public Long getDepartmentId()      { return departmentId; }
    public boolean isAvailable()       { return available; }

    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public void setAvailable(boolean available)     { this.available = available; }
}
