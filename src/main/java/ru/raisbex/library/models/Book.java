package ru.raisbex.library.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 30 символов")
    @Column(name = "name")
    private String name; // Название книги.

    @NotEmpty(message = "Автор не должен быть пустым")
    @Size(min = 2, max = 30, message = "Автор должен содержать от 2 до 30 символов")
    @Column(name = "author")
    private String author; // Имя автора книги.

    @Min(value = 1500, message = "Возраст должен быть больше 0")
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

    @Column(name = "image_path")
    private String imagePath; // Путь к изображению книги

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return  year == book.year  && Objects.equals(name, book.name)
                && Objects.equals(author, book.author) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, year);
    }
}
