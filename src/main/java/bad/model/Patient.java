package bad.model;


/*
 ❌ KÖTÜ:Neden kötü? role gibi alan yok; specialization için Department varken String kullanımı, public alan.
 */
public class Patient extends Person{

    public String phone;  // ❌ KÖTÜ: Regex yok, public alan
    public String email;  // ❌ KÖTÜ: format kontrolü, public alan
    public String insurance; // ❌ KÖTÜ: Sabit değerler, enum yoktur, public alan
}
