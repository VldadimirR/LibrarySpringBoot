package ru.raisbex.library.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.RegistrationService;
import ru.raisbex.library.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator) {
        this.registrationService = registrationService;
        this.personValidator = personValidator;
    }

    // Этот метод обрабатывает GET-запрос для отображения страницы входа.
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(name = "error", required = false) String error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/profile"; // Перенаправление на страницу профиля, если пользователь уже аутентифицирован.
        }
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "auth/login"; // Возврат страницы входа.
    }

    // Этот метод обрабатывает GET-запрос для отображения страницы регистрации.
    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person) {
        return "auth/registration"; // Возврат страницы регистрации.
    }

    // Этот метод обрабатывает POST-запрос для обработки регистрации пользователя.
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person,
                                      BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration"; // Возврат страницы регистрации с ошибками валидации, если они есть.

        registrationService.register(person); // Регистрация пользователя.

        return "redirect:/auth/login"; // Перенаправление на страницу входа после успешной регистрации.
    }
}
