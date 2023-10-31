package ru.raisbex.library.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

import java.util.List;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Уникальный идентификатор пользователя.

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно содержать от 2 до 30 символов")
    @Column(name = "FIO")
    private String FIO; // Полное имя пользователя.

    @Min(value = 1900, message = "Возраст должен быть больше 1900 лет")
    @Column(name = "age")
    private int age; // Возраст пользователя.

    @NotEmpty(message = "Login should not be empty")
    @Size(min = 4, max = 30, message = "Login should be between 4 and 30 characters")
    @Column(name = "login")
    private String login;

    @NotEmpty(message = "Password should not be empty")
    @Size(min = 6, message = "Password should be between 6 and 30 characters")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "owner")
    private List<Book> books; // Список книг, которые принадлежат данному пользователю.

    public Person() {

    }

    public Person(String FIO, int age) {
        this.FIO = FIO;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
