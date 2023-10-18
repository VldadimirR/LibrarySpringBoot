package ru.raisbex.library.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 30 characters")
    @Column(name = "name")
    private String name; // Название книги.

    @NotEmpty(message = "Author should not be empty")
    @Size(min = 2, max = 30, message = "Author should be between 2 and 30 characters")
    @Column(name = "author")
    private String author; // Имя автора книги.

    @Min(value = 1500, message = "Age should be greater than 0")
    @Column(name = "year")
    private int year; // Год выпуска книги.

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner; // Владелец книги, связь с классом Person.

    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeAt; // Время, когда книга была взята пользователями.

    @Transient // Поле не отображается в базе данных, так как является вычисляемым.
    private boolean expired; // Флаг, указывающий, просрочена ли аренда книги.

    public Book(){

    }

    public Book(String name, String author, int year) {
        this.name = name;
        this.author = author;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Date getTimeAt() {
        return timeAt;
    }

    public void setTimeAt(Date timeAt) {
        this.timeAt = timeAt;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
