package ru.raisbex.library.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.BookService;
import ru.raisbex.library.services.PeopleService;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;


@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BookController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    // Метод для отображения списка всех книг с пагинацией и сортировкой.
    @GetMapping()
    public String indexBook(Model model,
                            @RequestParam(defaultValue = "1", required = false) int page,
                            @RequestParam(defaultValue = "6", required = false) int perPage,
                            @RequestParam(defaultValue = "year") String sort) {
        // Получение списка книг с учетом параметров страницы, количества на странице и сортировки
        Page<Book> bookPage = bookService.index(page - 1, perPage, sort);

        // Передача списка книг и информации о пагинации в модель
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("perPage", perPage);
        model.addAttribute("totalPages", bookPage.getTotalPages());

        Set<String> userRoles = peopleService.getUserRoles();
        model.addAttribute("userRoles", userRoles);

        // Возвращает имя шаблона представления для отображения списка книг
        return "books/index";
    }

    // Метод для отображения информации о книге по её ID.
    @GetMapping("/{id}")
    public String showBook(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        // Получение информации о книге по её ID
        Book book = bookService.show(id);

        // Получение информации о владельце книги, если она назначена
        Optional<Person> bookOwner = bookService.getBookOwner(id);

        // Передача информации о книге и её владельце (если есть) в модель
        model.addAttribute("book", book);
        model.addAttribute("isAdmin", peopleService.adminCheck());

        if (bookOwner.isPresent()) {
            model.addAttribute("owner", bookOwner.get());
        } else {
            // Если книга не назначена, передача списка всех пользователей
            model.addAttribute("people", peopleService.index());
        }

        // Возвращает имя шаблона представления для отображения информации о книге
        return "books/show";
    }

    // Метод для создания новой книги (отображение формы создания).
    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        // Возвращает имя шаблона представления для создания новой книги
        return "books/new";
    }

    // Метод для обработки создания новой книги.
    @PostMapping()
    public String createBook(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
         @RequestParam("imageFile") MultipartFile imageFile) {
        // Проверка наличия ошибок валидации
        if (bindingResult.hasErrors()) {
            return "books/new"; // Если есть ошибки, возвращаем форму создания с сообщениями об ошибках.
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            bookService.imgLoad(book,imageFile);
        }

        // Сохранение новой книги
        bookService.save(book);

        // Перенаправление пользователя на список всех книг
        return "redirect:/books";
    }

    // Метод для редактирования книги по её ID (отображение формы редактирования).
    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable("id") int id) {
        // Получение информации о книге по её ID
        Book book = bookService.show(id);

        // Передача информации о книге в модель
        model.addAttribute("book", book);

        // Возвращает имя шаблона представления для редактирования книги
        return "books/edit";
    }

    // Метод для обновления книги по её ID.
    @PostMapping("/{id}")
    public String updateBook(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                             @PathVariable("id") int id, @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam(value = "deleteImage", required = false) Boolean deleteImage,
                             Model model) {

        // Проверка наличия ошибок валидации
        if (bindingResult.hasErrors()) {
            return "books/edit"; // Если есть ошибки, возвращаем форму редактирования с сообщениями об ошибках.
        }

        // New image file provided, save it
        bookService.imgLoad(book, imageFile);

        model.addAttribute("deleteImage", deleteImage);

        // Проверка на наличие параметра deleteImage и его значения
        bookService.delImg(deleteImage, book);

        // Обновление данных книги
        bookService.update(id, book);

        // Перенаправление пользователя на список всех книг
        return "redirect:/books";
    }


    // Метод для удаления книги по её ID.
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        // Удаление книги по её ID
        bookService.delete(id);

        // Перенаправление пользователя на список всех книг
        return "redirect:/books";
    }

    // Метод для освобождения книги от пользователя.
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        // Освобождение книги от пользователя
        bookService.release(id);

        // Перенаправление пользователя на информацию о книге после освобождения
        return "redirect:/books/" + id;
    }

    // Метод для назначения книги пользователю.
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        // Назначение книги пользователю
        bookService.assign(id, selectedPerson);

        // Перенаправление пользователя на информацию о книге после назначения
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String search(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);
        // Этот метод отображает страницу поиска книг.
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String titlePrefix) {
        // Этот метод выполняет поиск книг по указанному заголовку (titlePrefix) и отображает результаты.
        model.addAttribute("books", bookService.findBooksByTitlePrefix(titlePrefix));
        return "books/search";
    }


}


