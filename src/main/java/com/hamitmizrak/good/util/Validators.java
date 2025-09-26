package com.hamitmizrak.good.util;

/*
 CLEAN-UP ÖZETİ
 ✅ Regex ile email/telefon/TCNo doğrulaması.
 ✅ Username & parola kuralları (asgarî seviye).
 ✅ Basit, bağımsız ve test edilebilir.
*/

import java.util.regex.Pattern;

public final class Validators {
    private Validators(){}

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[+]?[0-9 ()-]{7,20}$");
    private static final Pattern TCNO  = Pattern.compile("^[1-9][0-9]{10}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.-]{3,32}$");
    // En az 6 karakter, 1 harf + 1 rakam (eğitim amaçlı basit kural)
    private static final Pattern STRONG_PASS = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");

    public static boolean email(String s){ return s != null && EMAIL.matcher(s).matches(); }
    public static boolean phone(String s){ return s != null && PHONE.matcher(s).matches(); }
    public static boolean tc(String s){ return s != null && TCNO.matcher(s).matches(); }
    public static boolean username(String s){ return s != null && USERNAME.matcher(s).matches(); }
    public static boolean strongPassword(String s){ return s != null && STRONG_PASS.matcher(s).matches(); }
}
