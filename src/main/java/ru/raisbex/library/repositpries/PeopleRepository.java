package ru.raisbex.library.repositpries;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.raisbex.library.models.Person;

import java.util.Optional;


@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    // JpaRepository предоставляет стандартные методы для работы с сущностью Person, включая CRUD операции.

    Optional<Person> findByFIO(String FIO);
    // Метод для поиска пользователя по полному имени (FIO).
    // Позволяет выполнить поиск пользователя по заданному полному имени FIO и возвращает результат в Optional,
    // так как пользователь может быть не найден.

    Optional<Person> findByLogin(String username);
}
