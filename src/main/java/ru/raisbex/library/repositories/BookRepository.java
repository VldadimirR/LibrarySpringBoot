package ru.raisbex.library.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.raisbex.library.models.Book;

import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // JpaRepository предоставляет стандартные методы для работы с сущностью Book, включая CRUD операции.

    List<Book> findByNameStartingWith(String title);
    // Метод для поиска книг по начальным символам в названии.
    // Позволяет выполнить поиск книг, у которых название начинается с указанной строки title.
}

