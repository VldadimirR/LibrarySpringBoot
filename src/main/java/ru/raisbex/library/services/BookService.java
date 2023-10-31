package ru.raisbex.library.services;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositpries.BookRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    private static final String UPLOAD_DIR = "C:\\Users\\rasal\\OneDrive\\Desktop\\library\\src\\main\\upload\\static\\img\\bookImg\\";

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

    public long getTotalBooksCount() {
        return bookRepository.count();
    }

    public void imgLoad(Book book, MultipartFile imageFile){
        if (imageFile == null || imageFile.isEmpty()) {
            // No new image file provided, so use the existing image path
            String existingImagePath = show(book.getId()).getImagePath();
            book.setImagePath(existingImagePath);
        } else {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.write(filePath, imageFile.getBytes());

                book.setImagePath(fileName);
            } catch (IOException e) {
                // Обработка ошибки
            }
        }
    }

    public void delImg(Boolean deleteImage, Book book){
        if (deleteImage != null && deleteImage) {
            String imagePath = book.getImagePath();
            if (imagePath != null) {
                Path imageFilePath = Paths.get(UPLOAD_DIR, imagePath);
                try {
                    Files.deleteIfExists(imageFilePath);
                    book.setImagePath(null);
                    update(book.getId(), book);
                } catch (IOException e) {
                    System.err.println("Error deleting image file: " + e.getMessage()); // Error logging
                    e.printStackTrace();
                }
            }
        }
    }
}

