package com.hamitmizrak.good.util;

/*
 CLEAN-UP ÖZETİ
 ✅ Konsol IO sarmalayıcı
 ✅ askPasswordMasked: System.console() varsa gerçek masking (readPassword),
    yoksa IDE konsolları için güvenli fallback (uyarı ile normal okumaya düşer)
*/

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class ConsoleIO {
    private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

    private ConsoleIO(){}

    public static String ask(String prompt){
        System.out.print(prompt);
        return SC.nextLine();
    }

    public static long askLong(String prompt){
        while (true){
            try {
                System.out.print(prompt);
                return Long.parseLong(SC.nextLine().trim());
            } catch (NumberFormatException e){
                System.out.println("Lütfen sayısal değer giriniz.");
            }
        }
    }

    /** Parolayı maskelenmiş biçimde okur (mümkünse). */
    public static String askPasswordMasked(String prompt) {
        Console console = System.console();
        if (console != null) { // Gerçek terminal
            char[] ch = console.readPassword(prompt);
            return ch == null ? "" : new String(ch);
        }
        // IDE/Runner konsollarında çoğu zaman null gelir → fallback
        System.out.print(prompt + "(gizleme desteklenmiyor bu konsolda) ");
        return SC.nextLine();
    }
}
