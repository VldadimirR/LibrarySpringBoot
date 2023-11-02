package ru.raisbex.library.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.raisbex.library.models.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collection;
import java.util.Collections;

public class PersonDetailsTest {

    @Test
    public void testGetAuthorities() {
        Person person = new Person();
        person.setRole("ROLE_USER");

        PersonDetails personDetails = new PersonDetails(person);

        Collection<? extends GrantedAuthority> authorities = personDetails.getAuthorities();

        // Проверяем, что роль пользователя соответствует роли, возвращенной методом getAuthorities.
        assertEquals(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), authorities);
    }

    @Test
    public void testGetPassword() {
        Person person = new Person();
        person.setPassword("password");

        PersonDetails personDetails = new PersonDetails(person);

        String password = personDetails.getPassword();

        // Проверяем, что пароль пользователя соответствует паролю, возвращенному методом getPassword.
        assertEquals("password", password);
    }

    @Test
    public void testGetUsername() {
        Person person = new Person();
        person.setFIO("John Doe");

        PersonDetails personDetails = new PersonDetails(person);

        String username = personDetails.getUsername();

        // Проверяем, что имя пользователя (ФИО) соответствует имени, возвращенному методом getUsername.
        assertEquals("John Doe", username);
    }

    @Test
    public void testIsAccountNonExpired() {
        Person person = new Person();
        PersonDetails personDetails = new PersonDetails(person);

        boolean accountNonExpired = personDetails.isAccountNonExpired();

        // Проверяем, что срок действия учетной записи всегда true.
        assertEquals(true, accountNonExpired);
    }

    @Test
    public void testIsAccountNonLocked() {
        Person person = new Person();
        PersonDetails personDetails = new PersonDetails(person);

        boolean accountNonLocked = personDetails.isAccountNonLocked();

        // Проверяем, что учетная запись не заблокирована всегда true.
        assertEquals(true, accountNonLocked);
    }

    @Test
    public void testIsCredentialsNonExpired() {
        Person person = new Person();
        PersonDetails personDetails = new PersonDetails(person);

        boolean credentialsNonExpired = personDetails.isCredentialsNonExpired();

        // Проверяем, что срок действия учетных данных всегда true.
        assertEquals(true, credentialsNonExpired);
    }

    @Test
    public void testIsEnabled() {
        Person person = new Person();
        PersonDetails personDetails = new PersonDetails(person);

        boolean enabled = personDetails.isEnabled();

        // Проверяем, что учетная запись всегда включена (true).
        assertEquals(true, enabled);
    }

    @Test
    public void testGetPerson() {
        Person person = new Person();
        PersonDetails personDetails = new PersonDetails(person);

        Person retrievedPerson = personDetails.getPerson();

        // Проверяем, что объект Person, полученный с помощью getPerson, совпадает с исходным объектом Person.
        assertEquals(person, retrievedPerson);
    }
}
