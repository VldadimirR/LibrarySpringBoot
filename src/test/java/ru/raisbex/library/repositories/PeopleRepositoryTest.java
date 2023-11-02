package ru.raisbex.library.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.raisbex.library.models.Person;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PeopleRepositoryTest {

    @Autowired
    private PeopleRepository peopleRepository;

    private Person person;

    private Person admin;

    private Person user;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setFIO("John Doe");
        person.setAge(2000);
        person.setLogin("johndoe");
        person.setPassword("personperson");
        person.setRole("USER");

        admin = new Person();
        admin.setFIO("Admin User");
        admin.setLogin("Admin");
        admin.setPassword("adminadmin");
        admin.setAge(2000);
        admin.setRole("ADMIN");

        user = new Person();
        user.setFIO("Regular User");
        user.setLogin("user");
        user.setPassword("useruseruser");
        user.setAge(2000);
        user.setRole("USER");


    }

    @Test
    public void testFindByFIO() {
        // Сохраните объект person в репозиторий.
        peopleRepository.save(person);

        // Ищите человека по имени (FIO).
        Optional<Person> foundPerson = peopleRepository.findByFIO("John Doe");

        // Утверждение: проверка, что объект Person был найден.
        assertTrue(foundPerson.isPresent());
        // Утверждение: проверка, что логин объекта Person соответствует ожидаемому значению.
        assertEquals("johndoe", foundPerson.get().getLogin());
    }

    @Test
    public void testFindByLogin() {
        // Установите необходимые свойства объекта person.
        person.setFIO("Jane Smith");
        person.setLogin("janesmith");
        // Сохраните объект person в репозиторий.
        peopleRepository.save(person);

        // Ищите человека по логину.
        Optional<Person> foundPerson = peopleRepository.findByLogin("janesmith");

        // Утверждение: проверка, что объект Person был найден.
        assertTrue(foundPerson.isPresent());
        // Утверждение: проверка, что ФИО объекта Person соответствует ожидаемому значению.
        assertEquals("Jane Smith", foundPerson.get().getFIO());
    }

    @Test
    public void testFindByRole() {
        // Сохраните администратора и пользователя в репозиторий.
        peopleRepository.save(admin);
        peopleRepository.save(user);

        // Ищите пользователей по роли "USER".
        List<Person> users = peopleRepository.findByRole("USER");

        // Утверждение: проверка, что найден только один пользователь с ролью "USER".
        assertEquals(1, users.size());
        // Утверждение: проверка, что ФИО объекта Person соответствует ожидаемому значению.
        assertEquals("Regular User", users.get(0).getFIO());
    }



}
