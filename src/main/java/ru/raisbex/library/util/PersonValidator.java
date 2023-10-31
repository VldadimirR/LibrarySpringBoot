package ru.raisbex.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.raisbex.library.models.Person;
import ru.raisbex.library.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    // Сервис для работы с данными пользователей.
    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        // Метод для проверки, поддерживает ли валидатор определенный класс (Person).
        // aClass - класс, который необходимо проверить.
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        // Метод для выполнения валидации объекта Person.
        // o - объект, который нужно провалидировать.
        // errors - объект для записи ошибок валидации.

        Person person = (Person) o;

        if (peopleService.getPersonByName(person.getLogin()).isPresent()) {
            // Проверка на уникальность логина.
            errors.rejectValue("login", "", "Человек с таким логином уже существует");
        }
        if (peopleService.getPersonByName(person.getFIO()).isPresent()) {
            // Проверка на уникальность имени пользователя (ФИО).
            errors.rejectValue("FIO", "", "Человек с таким ФИО уже существует");
        }
    }
}

