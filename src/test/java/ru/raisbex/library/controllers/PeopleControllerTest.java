package ru.raisbex.library.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.PeopleService;
import ru.raisbex.library.services.RegistrationService;
import ru.raisbex.library.util.PersonValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PeopleControllerTest {

    @Mock
    private PeopleService peopleService;

    @Mock
    private PersonValidator personValidator;

    @Mock
    private RegistrationService registrationService;

    private PeopleController peopleController;

    @BeforeEach
    void setUp() {
        peopleService = mock(PeopleService.class);
        personValidator = mock(PersonValidator.class);
        registrationService = mock(RegistrationService.class);
        peopleController = new PeopleController(peopleService, personValidator, registrationService);
    }


    @Test
    void testIndex() {
        List<Person> people = new ArrayList<>();
        when(peopleService.index()).thenReturn(people);

        // Создаем экземпляр контроллера с замоканным peopleService и фиктивными зависимостями.
        PeopleController controller = new PeopleController(peopleService, null, null);
        Model model = mock(Model.class);

        // Вызываем метод контроллера, который тестируем.
        String viewName = controller.index(model);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("people/index", viewName);
        // Проверяем, что в модель был добавлен атрибут "people" с ожидаемым списком людей.
        verify(model).addAttribute("people", people);
    }

    @Test
    void testShow() {
        Person person = new Person();
        when(peopleService.show(1)).thenReturn(person);
        List<Book> books = new ArrayList<>();
        when(peopleService.getBooksByPersonId(1)).thenReturn(books);

        // Создаем экземпляр контроллера с замоканным peopleService и фиктивными зависимостями.
        PeopleController controller = new PeopleController(peopleService, null, null);
        Model model = mock(Model.class);

        // Вызываем метод контроллера, который тестируем.
        String viewName = controller.show(1, model);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("people/show", viewName);
        // Проверяем, что в модель были добавлены атрибуты "person" и "books" с ожидаемыми значениями.
        verify(model).addAttribute("person", person);
        verify(model).addAttribute("books", books);
    }

    @Test
    void testNewPerson() {
        // Создаем экземпляр контроллера с фиктивными зависимостями.
        PeopleController controller = new PeopleController(null, null, null);

        // Вызываем метод контроллера, который тестируем.
        String viewName = controller.newPerson(new Person());

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("people/new", viewName);
    }

    @Test
    void testCreate() {
        when(peopleService.index()).thenReturn(Collections.emptyList());

        // Создаем экземпляр контроллера с замоканным peopleService, personValidator и registrationService.
        PeopleController controller = new PeopleController(peopleService, personValidator, registrationService);
        Person person = new Person();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Вызываем метод контроллера, который тестируем.
        String viewName = controller.create(person, bindingResult);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("redirect:/people", viewName);
        // Проверяем, что метод save в registrationService был вызван с ожидаемым объектом Person.
        verify(registrationService).save(person);
    }

    @Test
    void testEdit() {
        int userId = 1;
        Person user = new Person();
        user.setId(userId);

        // Замокируем вызов метода show в peopleService, чтобы он вернул ожидаемого пользователя.
        when(peopleService.show(userId)).thenReturn(user);

        Model model = Mockito.mock(Model.class);
        model.addAttribute("person", user);

        // Создаем экземпляр контроллера и вызываем метод edit, который тестируем.
        String viewName = peopleController.edit(model, userId);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("people/edit", viewName);
    }

    @Test
    void testUpdate() {
        int userId = 1;
        Person user = new Person();
        user.setId(userId);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        // Замокируем вызов метода hasErrors в bindingResult, чтобы он вернул false, предполагая, что ошибок нет.
        when(bindingResult.hasErrors()).thenReturn(false);

        // Замокируем вызов метода show в peopleService, чтобы он вернул ожидаемого пользователя.
        when(peopleService.show(userId)).thenReturn(user);

        // Создаем экземпляр контроллера и вызываем метод update, который тестируем.
        String viewName = peopleController.update(user, bindingResult, userId);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("redirect:/people", viewName);

        // Проверяем, что метод update в registrationService был вызван с ожидаемыми параметрами.
        Mockito.verify(registrationService).update(userId, user);
    }

    @Test
    void testDelete() {
        int userId = 1;

        // Создаем экземпляр контроллера и вызываем метод delete, который тестируем.
        String viewName = peopleController.delete(userId);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("redirect:/people", viewName);

        // Проверяем, что метод delete в peopleService был вызван с ожидаемым идентификатором пользователя.
        Mockito.verify(peopleService).delete(userId);
    }




}
