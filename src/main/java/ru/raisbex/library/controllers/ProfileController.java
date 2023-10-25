package ru.raisbex.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.BookService;
import ru.raisbex.library.services.PeopleService;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final PeopleService peopleService;
    private final BookService bookService;

    @Autowired
    public ProfileController(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }

    // Метод для отображения личного кабинета текущего авторизованного пользователя.
    @GetMapping()
    public String viewProfile(Model model) {
        // Получение информации о текущем авторизованном пользователе
        Person currentUser = peopleService.loadAuthenticationPeople();
        List<Book> books = peopleService.getBooksByPersonId(currentUser.getId());

        List<Person> allUsers = peopleService.index();
        List<Person> allAdmins = peopleService.getAllAdmins();

        long totalBooksCount = bookService.getTotalBooksCount();

        // Передача информации о пользователе в модель
        model.addAttribute("person", currentUser);
        model.addAttribute("books", books);
        model.addAttribute("users", allUsers);
        model.addAttribute("admins", allAdmins);
        model.addAttribute("totalBooksCount", totalBooksCount);


        // Возвращает имя шаблона представления для отображения личного кабинета
        if (peopleService.adminCheck()) {
            return "profile/admin-profile";
        } else {
            return "profile/person-profile";
        }
    }

    // Метод для обновления роли пользователя
    @PostMapping("/updateRole")
    public String updateRole(@RequestParam("userId") int userId, @RequestParam("newRole") String newRole) {
        // Ваш код для обновления роли пользователя
        peopleService.updateUserRole(userId, newRole);

        // После обновления роли перенаправляем обратно на страницу профиля
        return "redirect:/profile";
    }
}
