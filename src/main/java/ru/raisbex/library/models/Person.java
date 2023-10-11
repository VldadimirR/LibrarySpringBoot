package ru.raisbex.library.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Уникальный идентификатор пользователя.

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    @Column(name = "FIO")
    private String FIO; // Полное имя пользователя.

    @Min(value = 1900, message = "Age should be greater than 1900")
    @Column(name = "age")
    private int age; // Возраст пользователя.

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


}
