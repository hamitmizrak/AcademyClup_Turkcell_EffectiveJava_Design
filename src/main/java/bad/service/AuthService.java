package bad.service;



import com.hamitmizrak.bad.auth.User;
import com.hamitmizrak.bad.repository.UserRepository;
import com.hamitmizrak.bad.security.SecurityUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    // Kötü: Global state, bellek içi token store
    public static Map<String, User> SESSIONS = new HashMap<>();
    public static User CURRENT = null;

    public static void init() { UserRepository.init(); }

    public static Long register(User u) {
        // Kötü: validation eksik, collision kontrolü yok
        u.password = SecurityUtil.encodePassword(u.password); // Kötü: hash değil
        return UserRepository.save(u);
    }

    public static String login(String username, String rawPassword) {
        String enc = SecurityUtil.encodePassword(rawPassword);
        User found = UserRepository.findByUsernameAndPassword(username, enc);
        if (found == null) return null;
        String t = SecurityUtil.insecureToken();
        SESSIONS.put(t, found);
        CURRENT = found; // Kötü: proses globali
        return t;
    }

    public static User whoAmI(String token) {
        return SESSIONS.get(token); // Kötü: expiry yok, yetkilendirme yok
    }
}
