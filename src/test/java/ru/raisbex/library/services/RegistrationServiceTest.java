package ru.raisbex.library.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositories.PeopleRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTest {

    @Test
    public void testRegister() {
        // Создание мок-объектов репозитория и PasswordEncoder, и создание сервиса регистрации.
        PeopleRepository peopleRepository = mock(PeopleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RegistrationService registrationService = new RegistrationService(peopleRepository, passwordEncoder);

        // Создание объекта Person для регистрации.
        Person person = new Person();
        person.setLogin("testUser");
        person.setPassword("password");

        // Мокирование шифрования пароля.
        when(passwordEncoder.encode(person.getPassword())).thenReturn("encodedPassword");

        // Вызов метода регистрации.
        registrationService.register(person);

        // Проверка, что метод save был вызван с ожидаемым объектом person.
        verify(peopleRepository, times(1)).save(person);
    }

    @Test
    public void testSave() {
        // Создание мок-объектов репозитория и PasswordEncoder, и создание сервиса регистрации.
        PeopleRepository peopleRepository = mock(PeopleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RegistrationService registrationService = new RegistrationService(peopleRepository, passwordEncoder);

        // Создание объекта Person для сохранения.
        Person person = new Person();
        person.setLogin("testUser");
        person.setPassword("password");

        // Мокирование шифрования пароля.
        when(passwordEncoder.encode(person.getPassword())).thenReturn("encodedPassword");

        // Вызов метода сохранения.
        registrationService.save(person);

        // Проверка, что метод save был вызван с ожидаемым объектом person.
        verify(peopleRepository, times(1)).save(person);

        // Проверка, что пароль был зашифрован.
        assertEquals("encodedPassword", person.getPassword());

        // Проверка, что роль установлена на "ROLE_USER".
        assertEquals("ROLE_USER", person.getRole());
    }

    @Test
    public void testUpdate() {
        // Создание мок-объектов репозитория и PasswordEncoder, и создание сервиса регистрации.
        PeopleRepository peopleRepository = mock(PeopleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RegistrationService registrationService = new RegistrationService(peopleRepository, passwordEncoder);

        // Идентификатор пользователя, которого нужно обновить.
        int userId = 1;

        // Создание объекта Person с обновленными данными.
        Person personUpdate = new Person();
        personUpdate.setLogin("updatedUser");
        personUpdate.setPassword("newPassword");

        // Мокирование шифрования пароля.
        when(passwordEncoder.encode(personUpdate.getPassword())).thenReturn("newEncodedPassword");

        // Вызов метода обновления.
        registrationService.update(userId, personUpdate);

        // Проверка, что метод save был вызван с ожидаемым объектом personUpdate.
        verify(peopleRepository, times(1)).save(personUpdate);

        // Проверка, что ID был установлен на ожидаемое значение.
        assertEquals(userId, personUpdate.getId());

        // Проверка, что пароль был зашифрован.
        assertEquals("newEncodedPassword", personUpdate.getPassword());

        // Проверка, что роль установлена на "ROLE_USER".
        assertEquals("ROLE_USER", personUpdate.getRole());
    }

}
