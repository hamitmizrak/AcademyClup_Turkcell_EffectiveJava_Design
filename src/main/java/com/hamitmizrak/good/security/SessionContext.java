package com.hamitmizrak.good.security;

/*
 CLEAN-UP ÖZETİ
 ✅ CLI için hafif "session" katmanı (process-scope).
 ✅ AtomicReference ile thread-safe erişim (ileri seviye CLI’larda gerekebilir).
*/

import com.hamitmizrak.good.domain.Role;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class SessionContext {
    private static final AtomicReference<UserSession> CURRENT = new AtomicReference<>();

    private SessionContext(){}

    public static void login(long userId, String username, Role role){
        CURRENT.set(new UserSession(userId, username, role));
    }
    public static void logout(){ CURRENT.set(null); }
    public static Optional<UserSession> current(){ return Optional.ofNullable(CURRENT.get()); }

    public record UserSession(long id, String username, Role role){}
}
