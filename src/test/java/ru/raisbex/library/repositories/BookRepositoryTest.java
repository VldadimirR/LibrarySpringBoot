package ru.raisbex.library.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.raisbex.library.models.Book;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testFindByNameStartingWith() {
        // Создаем несколько книг с разными названиями
        Book book1 = new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling",2000);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien",1994);
        Book book3 = new Book("The Catcher in the Rye", "J.D. Salinger", 1956);

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // Вызываем метод поиска по начальным символам
        List<Book> foundBooks = bookRepository.findByNameStartingWith("The");

        // Проверяем, что список найденных книг не пустой и содержит ожидаемые книги
        assertEquals(2, foundBooks.size());
        assertEquals("The Hobbit", foundBooks.get(0).getName());
        assertEquals("The Catcher in the Rye", foundBooks.get(1).getName());
    }
}
