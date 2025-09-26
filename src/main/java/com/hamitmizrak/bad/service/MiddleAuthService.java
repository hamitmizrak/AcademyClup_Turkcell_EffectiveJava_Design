package com.hamitmizrak.bad.service;



import com.hamitmizrak.bad.auth.AuthUser;
import com.hamitmizrak.bad.repository.DataUserRepository;
import com.hamitmizrak.bad.security.PermissionSecurityUtil;

import java.util.HashMap;
import java.util.Map;

public class MiddleAuthService {
    // Kötü: Global state, bellek içi token store
    public static Map<String, AuthUser> SESSIONS = new HashMap<>();
    public static AuthUser CURRENT = null;

    public static void init() { DataUserRepository.init(); }

    public static Long register(AuthUser u) {
        // Kötü: validation eksik, collision kontrolü yok
        u.password = PermissionSecurityUtil.encodePassword(u.password); // Kötü: hash değil
        return DataUserRepository.save(u);
    }

    public static String login(String username, String rawPassword) {
        String enc = PermissionSecurityUtil.encodePassword(rawPassword);
        AuthUser found = DataUserRepository.findByUsernameAndPassword(username, enc);
        if (found == null) return null;
        String t = PermissionSecurityUtil.insecureToken();
        SESSIONS.put(t, found);
        CURRENT = found; // Kötü: proses globali
        return t;
    }

    public static AuthUser whoAmI(String token) {
        return SESSIONS.get(token); // Kötü: expiry yok, yetkilendirme yok
    }
}
