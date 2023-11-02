package ru.raisbex.library.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.BookService;
import ru.raisbex.library.services.PeopleService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProfileControllerTest {

    private ProfileController profileController;
    private PeopleService peopleService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        peopleService = mock(PeopleService.class);
        bookService = mock(BookService.class);
        profileController = new ProfileController(peopleService, bookService);
    }

    @Test
    void testViewProfileAdmin() {
        Model model = mock(Model.class);
        Person currentUser = new Person();
        currentUser.setId(1);

        List<Book> books = new ArrayList<>();
        books.add(new Book());

        List<Person> allUsers = new ArrayList<>();
        List<Person> allAdmins = new ArrayList<>();
        long totalBooksCount = 10;

        // Замокируем вызовы методов в peopleService и bookService, чтобы они возвращали ожидаемые значения.
        when(peopleService.loadAuthenticationPeople()).thenReturn(currentUser);
        when(peopleService.getBooksByPersonId(currentUser.getId())).thenReturn(books);
        when(peopleService.index()).thenReturn(allUsers);
        when(peopleService.getAllAdmins()).thenReturn(allAdmins);
        when(bookService.getTotalBooksCount()).thenReturn(totalBooksCount);
        when(peopleService.adminCheck()).thenReturn(true);

        // Создаем экземпляр контроллера и вызываем метод viewProfile, который тестируем.
        String viewName = profileController.viewProfile(model);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("profile/admin-profile", viewName);

        // Проверяем, что метод addAttribute в объекте model был вызван с ожидаемыми параметрами.
        verify(model).addAttribute("person", currentUser);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("users", allUsers);
        verify(model).addAttribute("admins", allAdmins);
        verify(model).addAttribute("totalBooksCount", totalBooksCount);

        // Проверяем, что методы в peopleService и bookService были вызваны соответствующее количество раз.
        verify(peopleService, times(1)).loadAuthenticationPeople();
        verify(peopleService, times(1)).getBooksByPersonId(currentUser.getId());
        verify(peopleService, times(1)).index();
        verify(peopleService, times(1)).getAllAdmins();
        verify(bookService, times(1)).getTotalBooksCount();
    }


    @Test
    void testViewProfilePerson() {
        Model model = mock(Model.class);
        Person currentUser = new Person();
        currentUser.setId(1);

        List<Book> books = new ArrayList<>();
        books.add(new Book());

        List<Person> allUsers = new ArrayList<>();
        List<Person> allAdmins = new ArrayList<>();
        long totalBooksCount = 10;

        // Замокируем вызовы методов в peopleService и bookService, чтобы они возвращали ожидаемые значения.
        when(peopleService.loadAuthenticationPeople()).thenReturn(currentUser);
        when(peopleService.getBooksByPersonId(currentUser.getId())).thenReturn(books);
        when(peopleService.index()).thenReturn(allUsers);
        when(peopleService.getAllAdmins()).thenReturn(allAdmins);
        when(bookService.getTotalBooksCount()).thenReturn(totalBooksCount);
        when(peopleService.adminCheck()).thenReturn(false);

        // Создаем экземпляр контроллера и вызываем метод viewProfile, который тестируем.
        String viewName = profileController.viewProfile(model);

        // Проверяем, что вернутое имя представления соответствует ожидаемому.
        assertEquals("profile/person-profile", viewName);

        // Проверяем, что метод addAttribute в объекте model был вызван с ожидаемыми параметрами.
        verify(model).addAttribute("person", currentUser);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("users", allUsers);
        verify(model).addAttribute("admins", allAdmins);
        verify(model).addAttribute("totalBooksCount", totalBooksCount);

        // Проверяем, что методы в peopleService и bookService были вызваны соответствующее количество раз.
        verify(peopleService, times(1)).loadAuthenticationPeople();
        verify(peopleService, times(1)).getBooksByPersonId(currentUser.getId());
        verify(peopleService, times(1)).index();
        verify(peopleService, times(1)).getAllAdmins();
        verify(bookService, times(1)).getTotalBooksCount();
    }


    @Test
    void testUpdateRole() {
        int userId = 1;
        String newRole = "admin";

        String result = profileController.updateRole(userId, newRole);

        assertEquals("redirect:/profile", result);

        verify(peopleService, times(1)).updateUserRole(userId, newRole);
    }
}
