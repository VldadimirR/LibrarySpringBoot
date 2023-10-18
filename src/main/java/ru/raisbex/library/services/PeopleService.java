package ru.raisbex.library.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositpries.PeopleRepository;
import ru.raisbex.library.security.PersonDetails;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final int DELAY_PERIOD = 864_000_000; // Период задержки в миллисекундах (10 дней).
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> index() {
        // Метод для получения списка всех пользователей.
        // Возвращает список объектов Person.
        return peopleRepository.findAll();
    }

    public Person show(int id) {
        // Метод для получения информации о пользователе по его ID.
        // id - ID пользователя, по которому выполняется поиск.
        // Возвращает найденного пользователя или null, если пользователь не найден.
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        // Метод для сохранения нового пользователя в базе данных.
        // person - объект пользователя для сохранения.
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person personUpdate) {
        // Метод для обновления информации о пользователе.
        // id - ID пользователя, personUpdate - обновленная информация о пользователе.
        // Выполняет обновление данных пользователя в базе данных.
        personUpdate.setId(id);
        peopleRepository.save(personUpdate);
    }

    @Transactional
    public void delete(int id) {
        // Метод для удаления пользователя по его ID.
        // id - ID пользователя, которого нужно удалить.
        peopleRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id) {
        // Метод для получения списка книг, назначенных пользователю по его ID.
        // id - ID пользователя, для которого нужно получить список книг.
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            // Инициализация загруженных книг пользователя.
            person.get().getBooks().forEach(this::checkDelay);
            // Проверка на просроченность книг.
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<Person> getPersonByName(String name) {
        // Метод для поиска пользователя по его имени (ФИО).
        // name - имя пользователя, по которому выполняется поиск.
        return peopleRepository.findByFIO(name);
    }

    public void checkDelay(Book book) {
        // Метод для проверки просроченности книги и установки флага просроченной книги.
        // book - книга, которую нужно проверить.
        if (new Date().getTime() - book.getTimeAt().getTime() > DELAY_PERIOD) {
            book.setExpired(true);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByLogin(s);

        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new PersonDetails(person.get());
    }


}
