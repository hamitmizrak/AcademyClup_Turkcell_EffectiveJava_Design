package com.hamitmizrak.bad.model;


import com.hamitmizrak.bad.common.CommonAudited;

import java.io.Serializable;
import java.util.Date;

/*
 ❌ KÖTÜ:
 Ne işe yarıyor? Ortak kişi alanları.
Neden kötü? public alanlar, equals/hashCode yok, validation yok.
 */

@CommonAudited("entity") // Kötü: hiçbir etkisi yok
public class ModelPerson implements Serializable { // Kötü: Serializable ama serialVersionUID yok
    // public static final Long serialVersionUID = 1L;
    // public static final Long serialVersionUID = 15415511515544L;
    public Long id;              // Kötü: public alan
    public String name;          // Kötü: null kontrolleri yok
    public String nationalId;    // Kötü: regex/format doğrulaması yok
    public Date birthDate;       // Kötü: java.time yerine eski Date

    public ModelPerson() {
    } // Kötü: anlamsız boş ctor

    // Kötü: setter/getter yok, doğrudan field erişimi teşvik ediliyor
}

