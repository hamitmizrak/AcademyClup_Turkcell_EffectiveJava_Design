package com.hamitmizrak.bad.security;



import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

public class PermissionSecurityUtil {
    private static final Random R = new Random(); // Kötü: güvenli değil (predictable)

    // Kötü: Base64 şifreleme değildir, sadece kodlama
    public static String encodePassword(String raw) {
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    // Kötü: Token tahmin edilebilir
    public static String insecureToken() {
        return Long.toHexString(R.nextLong()) + Long.toHexString(R.nextLong());
    }
}
