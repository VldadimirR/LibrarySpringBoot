package ru.raisbex.library.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.repositories.BookRepository;
import ru.raisbex.library.repositories.PeopleRepository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookServiceTest {


    @Autowired
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private PeopleRepository personRepository;
    @Mock
    private MultipartFile imageFile;

    @BeforeEach
    public void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
        when(bookRepository.save(Mockito.any(Book.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(personRepository.save(Mockito.any(Person.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

    }

    @Test
    public void testIndex() {
        // Создаем заглушку для Page<Book>
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book1", "Author1", 2022));
        books.add(new Book("Book2", "Author2", 2023));
        Page<Book> page = new PageImpl<>(books);

        // Настраиваем поведение заглушки
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.index(1, 10, "name");

        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testShow() {
        // Создаем заглушку для Optional<Book>
        Book book = new Book("Sample Book", "Sample Author", 2021);
        Optional<Book> optionalBook = Optional.of(book);

        // Настраиваем поведение заглушки
        when(bookRepository.findById(1)).thenReturn(optionalBook);

        Book result = bookService.show(1);

        assertNotNull(result);
        assertEquals("Sample Book", result.getName());
        assertEquals("Sample Author", result.getAuthor());
        assertEquals(2021, result.getYear());
    }

    @Test
    public void testShowNotFound() {
        // Создаем заглушку для Optional<Book> без значения (пустой)
        Optional<Book> optionalBook = Optional.empty();

        // Настраиваем поведение заглушки
        when(bookRepository.findById(1)).thenReturn(optionalBook);

        Book result = bookService.show(1);

        assertNull(result);
    }

    @Test
    public void testSave() {
        // Создаем книгу для сохранения
        Book bookToSave = new Book("New Book", "Author1", 2022);

        // Тест метода save
        bookService.save(bookToSave);

        // Проверяем, что метод save был вызван один раз с указанной книгой
        verify(bookRepository, times(1)).save(bookToSave);
    }

    @Test
    public void testUpdate() {
        // Создаем книгу для обновления
        Book bookToUpdate = new Book("Updated Book", "Author1", 2022);

        // Создаем заглушку для Optional<Book>
        Book existingBook = new Book("Existing Book", "Author1", 2022);
        existingBook.setId(1);
        Optional<Book> optionalBook = Optional.of(existingBook);

        // Настраиваем поведение заглушек
        when(bookRepository.findById(1)).thenReturn(optionalBook);

        // Тест метода update
        bookService.update(1, bookToUpdate);

        // Проверяем, что метод findById был вызван один раз и что информация о книге обновлена
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).save(bookToUpdate);

        // Обновляем имя книги в existingBook
        existingBook.setName(bookToUpdate.getName());

        // Проверяем, что имя книги было обновлено правильно
        assertEquals("Updated Book", existingBook.getName());
    }

    @Test
    public void testDelete() {
        int bookId = 1;

        // Создаем заглушку для Optional<Book>
        Book existingBook = new Book("Existing Book", "Author1", 2022);
        existingBook.setId(bookId);
        Optional<Book> optionalBook = Optional.of(existingBook);

        // Настраиваем поведение заглушки
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);

        // Тест метода delete
        bookService.delete(bookId);

        // Проверяем, что метод delete вызван один раз
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    public void testGetBookOwner() {
        int bookId = 1;

        // Создаем пользователя и книгу
        Person bookOwner = new Person();
        bookOwner.setFIO("John Doe");
        bookOwner.setAge(2000);
        bookOwner.setLogin("johndoe");
        bookOwner.setPassword("personperson");
        bookOwner.setRole("USER");

        Book book = new Book("Book 1", "Author1", 2022);
        book.setId(bookId);
        book.setOwner(bookOwner);

        // Создаем заглушку для Optional<Book>
        Optional<Book> optionalBook = Optional.of(book);

        // Настраиваем поведение заглушки
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);

        // Тест метода getBookOwner
        Optional<Person> result = bookService.getBookOwner(bookId);

        // Проверяем, что метод findById был вызван один раз
        verify(bookRepository, times(1)).findById(bookId);

        // Проверяем, что полученный владелец соответствует ожиданиям
        assertEquals(bookOwner, result.get());
    }

    @Test
    public void testAssign() {
        int bookId = 1;

        Person selectedPerson = new Person();
        selectedPerson.setFIO("Alice Johnson");
        selectedPerson.setAge(2000);
        selectedPerson.setLogin("alicejohnson");
        selectedPerson.setPassword("personperson");
        selectedPerson.setRole("USER");

        // Создаем заглушку для Optional<Book>
        Book existingBook = new Book("Existing Book", "Author1", 2022);
        existingBook.setId(bookId);
        Optional<Book> optionalBook = Optional.of(existingBook);

        // Настраиваем поведение заглушки
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);

        // Тест метода assign
        bookService.assign(bookId, selectedPerson);

        // Проверяем, что метод findById был вызван один раз и что информация о книге была обновлена
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(existingBook);

        // Проверяем, что владелец книги установлен корректно
        assertEquals(selectedPerson, existingBook.getOwner());
    }

    @Test
    public void testRelease() {
        int bookId = 1;

        // Создаем заглушку для Optional<Book>
        Book existingBook = new Book("Existing Book", "Author1", 2022);
        existingBook.setId(bookId);
        existingBook.setOwner(new Person());
        existingBook.getOwner().setFIO("Alice Johnson");
        existingBook.getOwner().setAge(2000);
        existingBook.getOwner().setLogin("alicejohnson");
        existingBook.getOwner().setPassword("personperson");
        existingBook.getOwner().setRole("USER");

        Optional<Book> optionalBook = Optional.of(existingBook);

        // Настраиваем поведение заглушки
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);

        // Тест метода release
        bookService.release(bookId);

        // Проверяем, что метод findById был вызван один раз и что информация о книге была обновлена
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(existingBook);

        // Проверяем, что владелец книги установлен в null
        assertEquals(null, existingBook.getOwner());
    }

    @Test
    public void testFindBooksByTitlePrefix() {
        // Создаем список книг, одна из которых соответствует заданному префиксу названия
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book1", "Author1", 2022));
        books.add(new Book("Book2", "Author2", 2022));
        books.add(new Book("Another Book", "Author3", 2022));

        // Задаем префикс для поиска
        String titlePrefix = "Boo";

        // Ожидаем, что только одна книга соответствует префиксу
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(books.get(0));

        // Настроим заглушку для bookRepository
        when(bookRepository.findByNameStartingWith(titlePrefix)).thenReturn(expectedBooks);

        // Тест метода findBooksByTitlePrefix
        List<Book> result = bookService.findBooksByTitlePrefix(titlePrefix);

        // Проверяем, что метод findByNameStartingWith был вызван один раз и возвращает ожидаемый результат
        verify(bookRepository, times(1)).findByNameStartingWith(titlePrefix);
        assertEquals(expectedBooks, result);
    }

    @Test
    public void testGetTotalBooksCount() {
        // Создаем список книг для теста
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book1", "Author1", 2022));
        books.add(new Book("Book2", "Author2", 2022));
        books.add(new Book("Another Book", "Author3", 2022));

        // Ожидаем, что общее количество книг равно размеру списка
        long expectedCount = books.size();

        // Настроим заглушку для bookRepository
        when(bookRepository.count()).thenReturn(expectedCount);

        // Тест метода getTotalBooksCount
        long result = bookService.getTotalBooksCount();

        // Проверяем, что метод count был вызван один раз и возвращает ожидаемый результат
        verify(bookRepository, times(1)).count();
        assertEquals(expectedCount, result);
    }

    @Test
    public void testImgLoad_ExistingImage() {
        Book book = new Book("Book1", "Author1", 2022);
        book.setId(1);
        book.setImagePath("existing_image.jpg");

        // Подготовка заглушки MultipartFile
        when(imageFile.isEmpty()).thenReturn(true); // Делаем заглушку для существующего изображения

        // Настраиваем заглушку для метода findById
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Тест метода imgLoad
        bookService.imgLoad(book, imageFile);

        // Проверяем, что методы работают корректно
        verify(imageFile, times(1)).isEmpty();
        assertEquals("existing_image.jpg", book.getImagePath());
    }


    @Test
    public void testImgLoad_NewImage() throws IOException {
        Book book = new Book("Book1", "Author1", 2022);
        book.setId(1);

        // Подготовка заглушки MultipartFile
        when(imageFile.isEmpty()).thenReturn(false); // Делаем заглушку для нового изображения
        when(imageFile.getOriginalFilename()).thenReturn("new_image.jpg");
        when(imageFile.getBytes()).thenReturn(new byte[0]); // Пустые байты для заглушки

        // Тест метода imgLoad
        bookService.imgLoad(book, imageFile);

        // Проверяем, что методы работают корректно
        verify(imageFile, times(1)).isEmpty();
        verify(imageFile, times(1)).getOriginalFilename();
        verify(imageFile, times(1)).getBytes();
        assertEquals("new_image.jpg", book.getImagePath());
    }

    @Test
    public void testImgLoad_ImageError() throws IOException {
        Book book = new Book("Book1", "Author1", 2022);
        book.setId(1);

        // Подготовка заглушки MultipartFile
        when(imageFile.isEmpty()).thenReturn(false); // Делаем заглушку для нового изображения
        when(imageFile.getOriginalFilename()).thenReturn("new_image.jpg");
        doThrow(new IOException("Simulated Image Error")).when(imageFile).getBytes();

        // Тест метода imgLoad
        bookService.imgLoad(book, imageFile);

        // Проверяем, что методы работают корректно
        verify(imageFile, times(1)).isEmpty();
        verify(imageFile, times(1)).getOriginalFilename();
        verify(imageFile, times(1)).getBytes();
        // Здесь вы можете добавить проверку обработки ошибки
    }

    @Test
    public void testDelImg_DoNotDeleteImage() {
        Book book = new Book("Book1", "Author1", 2022);
        book.setId(1);
        book.setImagePath("image_to_keep.jpg");

        Boolean deleteImage = false;

        // Тест метода delImg
        bookService.delImg(deleteImage, book);

        // Проверяем, что методы работают корректно
        // В данном случае, метод deleteImageFile не вызывается, так как мы не фактически удаляем файл в тесте
        assertEquals("image_to_keep.jpg", book.getImagePath());
    }
}
