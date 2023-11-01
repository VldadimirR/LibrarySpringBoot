package ru.raisbex.library.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.raisbex.library.models.Person;

import java.util.Collection;
import java.util.Collections;

public class PersonDetails implements UserDetails {
    private final Person person;

    public PersonDetails(Person person) {
        this.person = person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Возвращает список ролей (GrantedAuthority), к которым принадлежит пользователь.
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));
    }

    @Override
    public String getPassword() {
        // Возвращает пароль пользователя.
        return this.person.getPassword();
    }

    @Override
    public String getUsername() {
        // Возвращает имя пользователя (в данном случае, ФИО).
        return this.person.getFIO();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Проверяет, истек ли срок действия учетной записи пользователя.
        return true; // Всегда возвращает true, предполагая, что срок действия учетной записи не истек.
    }

    @Override
    public boolean isAccountNonLocked() {
        // Проверяет, заблокирована ли учетная запись пользователя.
        return true; // Всегда возвращает true, предполагая, что учетная запись не заблокирована.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Проверяет, истек ли срок действия учетных данных пользователя.
        return true; // Всегда возвращает true, предполагая, что учетные данные не истекли.
    }

    @Override
    public boolean isEnabled() {
        // Проверяет, включена ли учетная запись пользователя.
        return true; // Всегда возвращает true, предполагая, что учетная запись включена.
    }

    // Нужно, чтобы получать данные аутентифицированного пользователя
    public Person getPerson() {
        return this.person;
    }
}

