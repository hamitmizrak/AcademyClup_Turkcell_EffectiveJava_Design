package com.hamitmizrak.good.security;

/*
 CLEAN-UP ÖZETİ
 ✅ Güvenli parola saklama: PBKDF2WithHmacSHA256 + salt + yüksek iterasyon.
 ✅ Effective Java: kaynak ve hata yönetimi, MessageDigest.isEqual ile zaman sabit karşılaştırma.
*/

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32; // 256 bit
    private static final int ITERATIONS = 120_000;

    private PasswordUtil(){}

    public static String hash(String raw) {
        try {
            byte[] salt = new byte[SALT_BYTES];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            byte[] hash = pbkdf2(raw.toCharArray(), salt, ITERATIONS, HASH_BYTES);
            return ITERATIONS + ":" + b64(salt) + ":" + b64(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    public static boolean verify(String raw, String stored) {
        try {
            String[] p = stored.split(":");
            int it = Integer.parseInt(p[0]);
            byte[] salt = Base64.getDecoder().decode(p[1]);
            byte[] expected = Base64.getDecoder().decode(p[2]);
            byte[] actual = pbkdf2(raw.toCharArray(), salt, it, expected.length);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pass, byte[] salt, int iter, int len) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(pass, salt, iter, len * 8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    private static String b64(byte[] b){ return Base64.getEncoder().encodeToString(b); }
}
