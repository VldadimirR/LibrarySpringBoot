package ru.raisbex.library.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.PeopleService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PersonValidatorTest {

    private  PeopleService peopleService;

    private PersonValidator personValidator;

    @BeforeEach
    public void setUp(){
         peopleService = mock(PeopleService.class);
         personValidator = new PersonValidator(peopleService);
    }

    @Test
    public void testSupports() {
        boolean supports = personValidator.supports(Person.class);
        assertTrue(supports);
    }

    @Test
    public void testValidate_UniqueLoginAndFIO() {
        // Создайте объект Person с уникальным логином и ФИО.
        Person person = new Person();
        person.setLogin("testUser");
        person.setFIO("John Doe");

        // Создайте объект для отслеживания ошибок.
        Errors errors = mock(Errors.class);

        // Замокируйте метод getPersonByName, чтобы он возвращал Optional.empty(),
        // что указывает на отсутствие существующих пользователей с таким логином и ФИО.
        when(peopleService.getPersonByName("testUser")).thenReturn(Optional.empty());
        when(peopleService.getPersonByName("John Doe")).thenReturn(Optional.empty());

        // Вызовите метод validate для тестирования.
        personValidator.validate(person, errors);

        // Проверьте, что не было зарегистрировано ошибок в объекте errors.
        verify(errors, times(0)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testValidate_DuplicateLogin() {
        // Создайте объект Person с уникальным логином и ФИО.
        Person person = new Person();
        person.setLogin("testUser");
        person.setFIO("John Doe");

        // Создайте объект для отслеживания ошибок.
        Errors errors = mock(Errors.class);

        // Замокируйте метод getPersonByName, чтобы он возвращал существующего пользователя для логина.
        when(peopleService.getPersonByName("testUser")).thenReturn(Optional.of(new Person()));

        // Вызовите метод validate для тестирования.
        personValidator.validate(person, errors);

        // Проверьте, что была зарегистрирована ошибка для поля логина.
        verify(errors, times(1)).rejectValue("login", "", "Человек с таким логином уже существует");
    }

    @Test
    public void testValidate_DuplicateFIO() {
        // Создайте объект Person с уникальным логином и ФИО.
        Person person = new Person();
        person.setLogin("testUser");
        person.setFIO("John Doe");

        // Создайте объект для отслеживания ошибок.
        Errors errors = mock(Errors.class);

        // Замокируйте метод getPersonByName, чтобы он возвращал существующего пользователя для ФИО.
        when(peopleService.getPersonByName("testUser")).thenReturn(Optional.empty());
        when(peopleService.getPersonByName("John Doe")).thenReturn(Optional.of(new Person()));

        // Вызовите метод validate для тестирования.
        personValidator.validate(person, errors);

        // Проверьте, что была зарегистрирована ошибка для поля ФИО.
        verify(errors, times(1)).rejectValue("FIO", "", "Человек с таким ФИО уже существует");
    }

}
