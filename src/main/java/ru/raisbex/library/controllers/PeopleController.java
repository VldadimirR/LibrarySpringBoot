package ru.raisbex.library.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.raisbex.library.models.Book;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.PeopleService;
import ru.raisbex.library.util.PersonValidator;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    // Метод для отображения списка всех пользователей.
    @GetMapping()
    public String index(Model model) {
        // Получение списка всех пользователей
        List<Person> people = peopleService.index();

        // Передача списка пользователей в модель для отображения
        model.addAttribute("people", people);

        // Возвращает имя шаблона представления для отображения списка пользователей
        return "people/index";
    }

    // Метод для отображения информации о пользователе по его ID.
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        // Получение пользователя по его ID
        Person person = peopleService.show(id);

        // Получение списка книг, принадлежащих этому пользователю
        List<Book> books = peopleService.getBooksByPersonId(id);

        // Передача информации о пользователе и его книгах в модель
        model.addAttribute("person", person);
        model.addAttribute("books", books);

        // Возвращает имя шаблона представления для отображения информации о пользователе
        return "people/show";
    }

    // Метод для создания нового пользователя (отображение формы создания).
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        // Возвращает имя шаблона представления для создания нового пользователя
        return "people/new";
    }

    // Метод для обработки создания нового пользователя.
    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        // Проверка наличия ошибок валидации
        if (bindingResult.hasErrors()) {
            return "people/new"; // Если есть ошибки, возвращаем форму создания с сообщениями об ошибках.
        }

        // Сохранение нового пользователя
        peopleService.save(person);

        // Перенаправление пользователя на список всех пользователей
        return "redirect:/people";
    }

    // Метод для редактирования пользователя по его ID (отображение формы редактирования).
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        // Получение пользователя по его ID
        Person person = peopleService.show(id);

        // Передача информации о пользователе в модель
        model.addAttribute("person", person);

        // Возвращает имя шаблона представления для редактирования пользователя
        return "people/edit";
    }

    // Метод для обновления пользователя по его ID.
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        // Проверка наличия ошибок валидации
        if (bindingResult.hasErrors()) {
            return "people/edit"; // Если есть ошибки, возвращаем форму редактирования с сообщениями об ошибках.
        }

        // Обновление данных пользователя
        peopleService.update(id, person);

        // Перенаправление пользователя на список всех пользователей
        return "redirect:/people";
    }

    // Метод для удаления пользователя по его ID.
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        // Удаление пользователя по его ID
        peopleService.delete(id);

        // Перенаправление пользователя на список всех пользователей
        return "redirect:/people";
    }
}


