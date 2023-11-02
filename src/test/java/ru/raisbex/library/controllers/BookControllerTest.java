package ru.raisbex.library.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.BookService;
import ru.raisbex.library.services.PeopleService;
import org.springframework.security.test.context.support.WithMockUser;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest()
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private BookController bookController;

    @MockBean
    private BookService bookService;

    @MockBean
    private PeopleService peopleService;

    @Autowired
    private MockMvc mockMvc;

    private Book expectedBook;

    private Person person;

    private List<Book> fakeBooks;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

        bookController = new BookController(bookService, peopleService);

        expectedBook = new Book();
        person = new Person();

        expectedBook.setId(1);
        expectedBook.setName("Sample Book");
        expectedBook.setAuthor("Sample Author");
        expectedBook.setYear(2023);

        fakeBooks = Arrays.asList(
                new Book(),
                new Book(),
                new Book()
        );

        fakeBooks.get(0).setId(1);
        fakeBooks.get(0).setName("Book 1");
        fakeBooks.get(0).setAuthor("Author 1");
        fakeBooks.get(0).setYear(2022);

        fakeBooks.get(1).setId(2);
        fakeBooks.get(1).setName("Book 2");
        fakeBooks.get(1).setAuthor("Author 2");
        fakeBooks.get(1).setYear(2023);

        fakeBooks.get(2).setId(3);
        fakeBooks.get(2).setName("Book 3");
        fakeBooks.get(2).setAuthor("Author 3");
        fakeBooks.get(2).setYear(2024);
    }

    @Test
    void testIndexBook() throws Exception {
        // Создание фейковой страницы книг для возврата из bookService.index.
        Page<Book> fakeBookPage = new PageImpl<>(fakeBooks, PageRequest.of(0, 3), fakeBooks.size());

        // Мокирование вызова метода index в bookService для возврата фейковой страницы книг.
        when(bookService.index(anyInt(), anyInt(), anyString())).thenReturn(fakeBookPage);

        // Выполнение GET-запроса на "/books".
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk()) // Проверка, что ответ имеет статус 200 (ОК).
                .andExpect(view().name("books/index")) // Проверка, что возвращается представление "books/index".
                .andExpect(model().attributeExists("books", "currentPage", "perPage", "totalPages", "userRoles"));
        // Проверка наличия атрибутов "books", "currentPage", "perPage", "totalPages", "userRoles" в модели.
    }

    @Test
    void testShowBook() throws Exception {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        Person owner = new Person();
        owner.setId(2);

        // Установка поведения mock-сервисов
        Mockito.when(bookService.show(bookId)).thenReturn(book);
        Mockito.when(bookService.getBookOwner(bookId)).thenReturn(Optional.of(owner));
        Mockito.when(peopleService.adminCheck()).thenReturn(true);

        // Моки для Model и ModelView
        Model model = Mockito.mock(Model.class);
        String modelAndView = bookController.showBook(bookId, model, person);

        // Проверки
        assertEquals("books/show", modelAndView);

    }

        @Test
    void testNewBook() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/new"));
    }

    @Test
    void testCreateBook() throws Exception {
        // Мокирование метода save в bookService, который не возвращает результат (void).
        doNothing().when(bookService).save(any(Book.class));

        // Создание фейкового файлового объекта MockMultipartFile.
        MockMultipartFile file = new MockMultipartFile("imageFile", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());

        // Выполнение мультимедийного POST-запроса на "/books" с передачей файла и параметров.
        mockMvc.perform(MockMvcRequestBuilders.multipart("/books")
                        .file(file)
                        .param("name", "Sample Book")
                        .param("author", "Sample Author")
                        .param("year", "2023")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ имеет статус 3xx (перенаправление).
                .andExpect(redirectedUrl("/books")); // Проверка, что произошло перенаправление на "/books".
    }

    @Test
    public void testEditBook() throws Exception {
        int bookId = 1;
        Book expectedBook = new Book();
        expectedBook.setId(bookId);
        expectedBook.setName("Sample Book");
        expectedBook.setAuthor("Sample Author");

        // Мокирование метода show в bookService для возврата фейковой книги.
        when(bookService.show(bookId)).thenReturn(expectedBook);

        // Выполнение GET-запроса на "/books/{id}/edit".
        MvcResult result = mockMvc.perform(get("/books/" + bookId + "/edit"))
                .andExpect(status().isOk()) // Проверка, что ответ имеет статус 200 (ОК).
                .andExpect(view().name("books/edit")) // Проверка, что возвращается представление "books/edit".
                .andExpect(model().attributeExists("book")) // Проверка наличия атрибута "book" в модели.
                .andExpect(model().attribute("book", expectedBook)) // Проверка, что атрибут "book" равен ожидаемой книге.
                .andReturn();
    }

    @Test
    public void testUpdateBook() throws Exception {
        int bookId = 1;

        // Создание фейкового файлового объекта MockMultipartFile.
        MockMultipartFile file = new MockMultipartFile("imageFile", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());

        // Выполнение мультимедийного POST-запроса на "/books/{id}" с передачей файла и параметров.
        mockMvc.perform(MockMvcRequestBuilders.multipart("/books/{id}", bookId)
                        .file(file)
                        .param("deleteImage", "true")
                        .flashAttr("book", expectedBook)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ имеет статус 3xx (перенаправление).
                .andExpect(redirectedUrl("/books?deleteImage=true")); // Проверка, что произошло перенаправление на "/books?deleteImage=true".

        // Проверка, что методы imgLoad, delImg и update в bookService были вызваны с ожидаемыми параметрами.
        verify(bookService).imgLoad(expectedBook, file);
        verify(bookService).delImg(true, expectedBook);
        verify(bookService).update(bookId, expectedBook);
    }


    @Test
    public void testDeleteBook() throws Exception {
        int bookId = 1;

        // Выполнение DELETE-запроса на "/books/{id}" для удаления книги с указанным ID.
        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ имеет статус 3xx (перенаправление).
                .andExpect(redirectedUrl("/books")); // Проверка, что произошло перенаправление на "/books".

        // Проверка, что метод delete в bookService был вызван с ожидаемым параметром (ID книги).
        verify(bookService).delete(bookId);
    }

    @Test
    public void testReleaseBook() throws Exception {
        // Выполнение PATCH-запроса на "/books/{id}/release" для освобождения книги.
        mockMvc.perform(MockMvcRequestBuilders.patch("/books/{id}/release", 1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ имеет статус 3xx (перенаправление).
                .andExpect(redirectedUrl("/books/1")); // Проверка, что произошло перенаправление на "/books/1".
    }

    @Test
    public void testAssignBook() throws Exception {
        // Выполнение PATCH-запроса на "/books/{id}/assign" для назначения книги определенному пользователю.
        mockMvc.perform(MockMvcRequestBuilders.patch("/books/{id}/assign", 1)
                        .param("selectedPerson.id", "2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ имеет статус 3xx (перенаправление).
                .andExpect(redirectedUrl("/books/1")); // Проверка, что произошло перенаправление на "/books/1".
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearchPage() throws Exception {
        // Выполнение GET-запроса на "/books/search" с ролью "ADMIN".
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search"))
                .andExpect(status().isOk()); // Предполагается, что возвращается HTTP 200 (ОК).
    }

    @Test
    public void testMakeSearch() throws Exception {
        // Выполнение POST-запроса на "/books/search" для выполнения поиска с заданным запросом.
        mockMvc.perform(MockMvcRequestBuilders.post("/books/search")
                        .param("query", "SampleTitle")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()); // Предполагается, что возвращается HTTP 200 (ОК).
    }

}



