package ru.raisbex.library.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositories.PeopleRepository;
import ru.raisbex.library.security.PersonDetails;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final int DELAY_PERIOD = 864_000_000; // Период задержки в миллисекундах (10 дней).
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    // Метод для получения списка всех пользователей.
    public List<Person> index() {
        return peopleRepository.findAll();
    }

    // Метод для получения информации о пользователе по его ID.
    public Person show(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    // Метод для удаления пользователя по его ID.
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    // Метод для получения списка книг, назначенных пользователю по его ID.
    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Person user = person.get();
            initializeUserBooks(user);
            checkForExpiredBooks(user);
            return user.getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    // Метод для инициализации загруженных книг пользователя.
    private void initializeUserBooks(Person user) {
        Hibernate.initialize(user.getBooks());
    }

    // Метод для проверки просроченных книг пользователя и установки флага просроченной книги.
    private void checkForExpiredBooks(Person user) {
        user.getBooks().forEach(this::checkDelay);
    }

    // Метод для поиска пользователя по его имени (ФИО).
    public Optional<Person> getPersonByName(String name) {
        return peopleRepository.findByFIO(name);
    }

    // Метод для проверки просроченности книги и установки флага просроченной книги.
    private void checkDelay(Book book) {
        Date timeAt = book.getTimeAt();
        if (timeAt != null && new Date().getTime() - timeAt.getTime() > DELAY_PERIOD) {
            book.setExpired(true);
        }
    }

    @Override
    // Метод для аутентификации пользователя.
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Person person = getUserByLogin(s);
        return createPersonDetails(person);
    }

    private Person getUserByLogin(String login) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByLogin(login);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return person.get();
    }

    private UserDetails createPersonDetails(Person person) {
        return new PersonDetails(person);
    }

    // Метод для получения ролей текущего пользователя.
    public Set<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    // Метод для проверки, является ли текущий пользователь администратором.
    public boolean adminCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    }

    private boolean adminCheck(Person person) {
        return "ROLE_ADMIN".equals(person.getRole());
    }

    // Метод для фильтрации администраторов из списка пользователей.
    public List<Person> filterOutAdmins(List<Person> allUsers) {
        return  allUsers.stream()
                .filter(person -> !adminCheck(person))
                .collect(Collectors.toList());
    }

    // Метод для получения информации о текущем аутентифицированном пользователе.
    public Person loadAuthenticationPeople() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Person> currentUserOptional = getPersonByName(authentication.getName());

        return currentUserOptional.orElse(null);
    }

    @Transactional
    // Метод для обновления роли пользователя.
    public void updateUserRole(int userId, String newRole) {
        Optional<Person> userOptional = peopleRepository.findById(userId);
        if (userOptional.isPresent()) {
            Person user = userOptional.get();
            user.setRole(newRole);
            peopleRepository.save(user);
        }
    }

    // Метод для получения всех администраторов.
    public List<Person> getAllAdmins() {
        return peopleRepository.findByRole("ROLE_ADMIN");
    }
}
