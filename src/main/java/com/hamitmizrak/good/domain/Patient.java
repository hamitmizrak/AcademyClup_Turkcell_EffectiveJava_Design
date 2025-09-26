package com.hamitmizrak.good.domain;

/*
 CLEAN-UP ÖZETİ
 ✅ Encapsulation ve anlamlı alanlar.
*/
public class Patient extends Person {
    private String phone;
    private String email;

    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
