package ru.raisbex.library.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositories.PeopleRepository;
import ru.raisbex.library.security.PersonDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PeopleServiceTest {

    @InjectMocks
    private PeopleService peopleService;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private Authentication authentication;

    private Person user1;

    private Person user2;

    @BeforeEach
    public void setUp(){
        user1 = new Person();
        user1.setFIO("User1");
        user1.setAge(25);
        user1.setLogin("user1");
        user1.setPassword("password1");
        user1.setRole("USER");

        user2 = new Person();
        user2.setFIO("User2");
        user2.setAge(30);
        user2.setLogin("user2");
        user2.setPassword("password2");
        user2.setRole("ADMIN");
    }

    @Test
    public void testIndex() {
        // Создаем список пользователей для заглушки
        List<Person> people = new ArrayList<>();
        people.add(user1);
        people.add(user2);


        // Настроим заглушку для peopleRepository.findAll()
        when(peopleRepository.findAll()).thenReturn(people);

        // Вызываем метод index() из сервиса
        List<Person> result = peopleService.index();

        // Проверяем, что результат совпадает с ожидаемым списком пользователей
        assertEquals(people, result);
    }

    @Test
    public void testShow_UserExists() {
        int userId = 1;

        // Настроим заглушку для peopleRepository.findById()
        when(peopleRepository.findById(userId)).thenReturn(Optional.of(user1));

        // Вызываем метод show() из сервиса
        Person result = peopleService.show(userId);

        // Проверяем, что результат совпадает с ожидаемым пользователем
        assertEquals(user1, result);
    }

    @Test
    public void testShow_UserNotExists() {
        int userId = 1;

        // Настроим заглушку для peopleRepository.findById()
        when(peopleRepository.findById(userId)).thenReturn(Optional.empty());

        // Вызываем метод show() из сервиса
        Person result = peopleService.show(userId);

        // Проверяем, что результат равен null, так как пользователя с таким ID нет
        assertNull(result);
    }

    @Test
    public void testDelete() {
        int userId = 1;

        // Вызываем метод delete() из сервиса
        peopleService.delete(userId);

        // Проверяем, что метод peopleRepository.deleteById() был вызван с правильным аргументом (ID пользователя)
        verify(peopleRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testGetBooksByPersonId_ExistingPerson() {
        int userId = 1;
        Person user = createUserWithBooks(userId);

        // Настроим заглушку для peopleRepository.findById()
        when(peopleRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Book> result = peopleService.getBooksByPersonId(userId);

        // Проверяем, что результат содержит книги пользователя
        assertEquals(user.getBooks(), result);
    }

    @Test
    public void testGetBooksByPersonId_NonExistingPerson() {
        int userId = 1;

        // Настроим заглушку для peopleRepository.findById()
        when(peopleRepository.findById(userId)).thenReturn(Optional.empty());

        List<Book> result = peopleService.getBooksByPersonId(userId);

        // Проверяем, что результат пустой, так как пользователя с таким ID нет
        assertTrue(result.isEmpty());
    }

    // Создать метод для создания пользователя с книгами в тестовых целях
    private Person createUserWithBooks(int userId) {
        Person user = new Person();
        user.setId(userId);
        user.setFIO("User1");
        user.setAge(25);
        user.setLogin("user1");
        user.setPassword("password1");
        user.setRole("USER");

        List<Book> books = new ArrayList<>();
        Book book1 = new Book("Book1", "Author1", 2022);
        Book book2 = new Book("Book2", "Author2", 2022);
        books.add(book1);
        books.add(book2);

        user.setBooks(books);

        return user;
    }

    @Test
    public void testGetPersonByName_ExistingPerson() {
        String userName = "User1";
        Person user = createUserWithBooks(1);

        // Настроим заглушку для peopleRepository.findByFIO()
        when(peopleRepository.findByFIO(userName)).thenReturn(Optional.of(user));

        Optional<Person> result = peopleService.getPersonByName(userName);

        // Проверяем, что результат содержит пользователя с заданным именем
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testGetPersonByName_NonExistingPerson() {
        String userName = "User1";

        // Настроим заглушку для peopleRepository.findByFIO()
        when(peopleRepository.findByFIO(userName)).thenReturn(Optional.empty());

        Optional<Person> result = peopleService.getPersonByName(userName);

        // Проверяем, что результат пустой, так как пользователя с таким именем нет
        assertFalse(result.isPresent());
    }

    @Test
    public void testLoadUserByUsername() {
        // Создание объекта PeopleService с мок-репозиторием.
        PeopleService peopleService = new PeopleService(peopleRepository);

        // Создание объекта Person для загрузки.
        Person person = new Person();
        person.setLogin("testUser");
        person.setPassword("password");
        person.setRole("ROLE_USER");

        // Создание объекта UserDetails на основе Person.
        UserDetails userDetails = new PersonDetails(person);

        // Мокирование репозитория для возвращения Person при поиске по логину.
        Mockito.when(peopleRepository.findByLogin("testUser")).thenReturn(Optional.of(person));

        // Загрузка пользователя и сравнение с ожидаемым UserDetails.
        UserDetails loadedUser = peopleService.loadUserByUsername("testUser");

        assertEquals(userDetails, loadedUser);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Создание объекта PeopleService с мок-репозиторием.
        PeopleService peopleService = new PeopleService(peopleRepository);

        // Мокирование репозитория для возвращения пустого Optional при поиске несуществующего пользователя.
        Mockito.when(peopleRepository.findByLogin("nonExistentUser")).thenReturn(Optional.empty());

        // Проверка, что загрузка пользователя, которого нет, вызывает исключение UsernameNotFoundException.
        assertThrows(UsernameNotFoundException.class, () -> {
            peopleService.loadUserByUsername("nonExistentUser");
        });
    }

    @Test
    public void testFilterOutAdmins() {
        // Создание двух объектов Person, один с ролью "ROLE_ADMIN", другой с ролью "ROLE_USER".
        Person admin1 = new Person();
        admin1.setRole("ROLE_ADMIN");

        Person user1 = new Person();
        user1.setRole("ROLE_USER");

        // Создание списка пользователей.
        List<Person> users = Arrays.asList(admin1, user1);

        // Вызов метода filterOutAdmins для фильтрации администраторов.
        List<Person> result = peopleService.filterOutAdmins(users);

        // Проверка, что в результате остался только один пользователь с ролью "ROLE_USER".
        assertEquals(1, result.size());
        assertEquals(user1, result.get(0));
    }


    @Test
    public void testLoadAuthenticationPeople() {
        // Создание объекта Person для пользователя.
        Person user = new Person();
        user.setLogin("testUser");

        // Мокирование объекта Authentication, который представляет аутентифицированного пользователя.
        when(authentication.getName()).thenReturn("testUser");

        // Установка аутентификации в SecurityContextHolder.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Мокирование репозитория для возврата пользователя при поиске по имени (FIO).
        when(peopleRepository.findByFIO("testUser")).thenReturn(Optional.of(user));

        // Вызов метода loadAuthenticationPeople и проверка результата.
        Person result = peopleService.loadAuthenticationPeople();

        // Убедитесь, что результат соответствует ожидаемому пользователю.
        assertEquals(user, result);
    }

    @Test
    public void testUpdateUserRole() {
        // Создание объекта Person с ролью "ROLE_USER".
        Person user = new Person();
        user.setId(1);
        user.setRole("ROLE_USER");

        // Мокирование репозитория для возврата пользователя при поиске по ID.
        when(peopleRepository.findById(1)).thenReturn(Optional.of(user));

        // Вызов метода updateUserRole для изменения роли пользователя.
        peopleService.updateUserRole(1, "ROLE_ADMIN");

        // Проверка, что роль пользователя обновлена и вызван метод сохранения в репозитории.
        assertEquals("ROLE_ADMIN", user.getRole());
        verify(peopleRepository, times(1)).save(user);
    }

    @Test
    public void testGetAllAdmins() {
        // Создание двух объектов Person с ролью "ROLE_ADMIN" и одного с ролью "ROLE_USER".
        Person admin1 = new Person();
        admin1.setRole("ROLE_ADMIN");

        Person admin2 = new Person();
        admin2.setRole("ROLE_ADMIN");

        Person user1 = new Person();
        user1.setRole("ROLE_USER");

        // Создание списка администраторов и пользователей.
        List<Person> admins = Arrays.asList(admin1, admin2);

        // Мокирование репозитория для возврата списка администраторов при поиске по роли "ROLE_ADMIN".
        when(peopleRepository.findByRole("ROLE_ADMIN")).thenReturn(admins);

        // Вызов метода getAllAdmins и проверка результата.
        List<Person> result = peopleService.getAllAdmins();

        // Проверка, что в результате есть два администратора и нет пользователей.
        assertEquals(2, result.size());
        assertTrue(result.contains(admin1));
        assertTrue(result.contains(admin2));
        assertFalse(result.contains(user1));
    }


}
