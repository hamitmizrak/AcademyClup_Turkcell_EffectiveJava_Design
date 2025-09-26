package bad.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validators {
    // Kötü: abartılı ve yanlış regex (örnek)
    public static boolean isEmail(String s) {
        try {
            Pattern p = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$", Pattern.DOTALL);
            Matcher m = p.matcher(s == null ? "" : s.trim());
            return m.find(); // Kötü: matches yerine find
        } catch (Exception e) { return true; } // Kötü: hata olursa true
    }

    // Kötü: TC no için yanlış/eksik kontrol (örnek)
    public static boolean isNationalId(String s) {
        Pattern p = Pattern.compile("^[1-9][0-9]{10}$");
        return p.matcher(s == null ? "" : s).matches();
    }

    // Kötü: telefon için esnek ama çok gevşek
    public static boolean isPhone(String s) {
        Pattern p = Pattern.compile("^[+0-9\\-() ]{7,20}$");
        return p.matcher(s == null ? "" : s).matches();
    }
}
