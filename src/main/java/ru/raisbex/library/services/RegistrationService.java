package ru.raisbex.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositpries.PeopleRepository;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для регистрации нового пользователя в базе данных.
    @Transactional
    public void register(Person person) {
        save(person);
    }

    @Transactional
    public void save(Person person) {
        // Метод для сохранения нового пользователя в базе данных.
        // person - объект пользователя для сохранения.
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person personUpdate) {
        // Метод для обновления информации о пользователе.
        // id - ID пользователя, personUpdate - обновленная информация о пользователе.
        // Выполняет обновление данных пользователя в базе данных.
        personUpdate.setId(id);
        personUpdate.setPassword(passwordEncoder.encode(personUpdate.getPassword()));
        personUpdate.setRole("ROLE_USER");
        peopleRepository.save(personUpdate);
    }
}
