package bad.model;

public class Doctor  extends Person{

    public String specialization;  // ❌ KÖTÜ: Regex yok, public alan
    public boolean avaiable;  // ❌ KÖTÜ: format kontrolü, public alan
}
