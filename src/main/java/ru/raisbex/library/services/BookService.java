package ru.raisbex.library.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositpries.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> index(int page, int size, String sortField) {
        // Метод для получения страницы книг с пагинацией и сортировкой.
        // page - номер страницы, size - количество записей на странице, sortField - поле для сортировки.
        // Возвращает объект Page, содержащий список книг на указанной странице.

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortField));
        return bookRepository.findAll(pageable);
    }

    public Book show(int id) {
        // Метод для получения информации о книге по её ID.
        // id - ID книги, по которому выполняется поиск.
        // Возвращает найденную книгу или null, если книга не найдена.
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        // Метод для сохранения новой книги в базе данных.
        // book - объект книги для сохранения.
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book bookUpdate) {
        // Метод для обновления информации о книге.
        // id - ID книги, bookUpdate - обновленная информация о книге.
        // Выполняет поиск книги по ID, обновляет информацию, и сохраняет обновленную книгу в базе данных.
        Book bookToBeUpdated = bookRepository.findById(id).get();
        bookUpdate.setId(id);
        bookUpdate.setOwner(bookToBeUpdated.getOwner());
        bookRepository.save(bookUpdate);
    }

    @Transactional
    public void delete(int id) {
        // Метод для удаления книги по её ID.
        // id - ID книги, которую необходимо удалить.
        bookRepository.deleteById(id);
    }

    public Optional<Person> getBookOwner(int id) {
        // Метод для получения владельца книги по её ID.
        // id - ID книги, для которой нужно найти владельца.
        return bookRepository.findById(id).map(Book::getOwner);
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        // Метод для назначения книги пользователю (передачи книги).
        // id - ID книги, selectedPerson - объект пользователя, которому передается книга.
        Book book = bookRepository.findById(id).get();
        book.setOwner(selectedPerson);
        bookRepository.save(book);
    }

    @Transactional
    public void release(int id) {
        // Метод для освобождения книги от пользователя (отмены передачи книги).
        // id - ID книги, которую нужно освободить.
        Book releasedBook = bookRepository.findById(id).get();
        releasedBook.setOwner(null);
        bookRepository.save(releasedBook);
    }

    public List<Book> findBooksByTitlePrefix(String titlePrefix) {
        // Метод для поиска книг по начальным символам названия.
        // titlePrefix - строка, задающая начальные символы названия книги.
        return bookRepository.findByNameStartingWith(titlePrefix);
    }
}

