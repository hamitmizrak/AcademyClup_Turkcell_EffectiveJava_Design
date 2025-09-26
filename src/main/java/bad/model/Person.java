package bad.model;


import com.hamitmizrak.bad.common.Audited;

import java.io.Serializable;
import java.util.Date;

/*
 ❌ KÖTÜ:
 Ne işe yarıyor? Ortak kişi alanları.
Neden kötü? public alanlar, equals/hashCode yok, validation yok.
 */

@Audited("entity") // Kötü: hiçbir etkisi yok
public class Person implements Serializable { // Kötü: Serializable ama serialVersionUID yok
    public Long id;              // Kötü: public alan
    public String name;          // Kötü: null kontrolleri yok
    public String nationalId;    // Kötü: regex/format doğrulaması yok
    public Date birthDate;       // Kötü: java.time yerine eski Date

    public Person() {} // Kötü: anlamsız boş ctor

    // Kötü: setter/getter yok, doğrudan field erişimi teşvik ediliyor
}

