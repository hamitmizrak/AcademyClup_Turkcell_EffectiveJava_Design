package com.hamitmizrak.good.service;

/*
 CLEAN-UP ÖZETİ
 ✅ Login: findByUsernameOrEmail kullanır
*/

import com.hamitmizrak.good.domain.Role;
import com.hamitmizrak.good.dto.LoginRequest;
import com.hamitmizrak.good.dto.RegisterRequest;
import com.hamitmizrak.good.repository.UserRepository;
import com.hamitmizrak.good.security.PasswordUtil;
import com.hamitmizrak.good.security.SessionContext;
import com.hamitmizrak.good.util.Validators;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AuthService {
    private final UserRepository users;
    private final ResourceBundle i18n;

    public AuthService(UserRepository users, Locale locale) {
        this.users = users;
        this.i18n = ResourceBundle.getBundle("messages", locale);
    }

    public long register(RegisterRequest req, Role role) throws SQLException {
        if (req == null) throw new IllegalArgumentException("req null");
        if (!Validators.email(req.email())) throw new IllegalArgumentException(i18n.getString("invalid.email"));
        if (!Validators.username(req.username())) throw new IllegalArgumentException(i18n.getString("invalid.username"));
        if (!Validators.strongPassword(req.password())) throw new IllegalArgumentException(i18n.getString("invalid.password"));

        if (users.findByUsername(req.username()).isPresent())
            throw new IllegalArgumentException(i18n.getString("conflict.username"));
        if (users.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException(i18n.getString("conflict.email"));

        String hash = PasswordUtil.hash(req.password());
        return users.insert(req.username(), req.email(), hash, role);
    }

    /** username veya email ile giriş. LoginRequest.username alanı "identifier" olarak kabul edilir. */
    public boolean login(LoginRequest req) throws SQLException {
        Optional<UserRepository.UserRow> row = users.findByUsernameOrEmail(req.username());
        if (row.isEmpty()) return false;
        var u = row.get();
        if (!u.enabled()) return false;
        if (!PasswordUtil.verify(req.password(), u.passwordHash())) return false;
        SessionContext.login(u.id(), u.username(), u.role());
        return true;
    }
}
