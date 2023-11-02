package ru.raisbex.library.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Sample Book", "Sample Author", 2023);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("Sample Book", book.getName());
        assertEquals("Sample Author", book.getAuthor());
        assertEquals(2023, book.getYear());

        book.setName("New Name");
        assertEquals("New Name", book.getName());

        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());

        book.setYear(2024);
        assertEquals(2024, book.getYear());
    }

    @Test
    void testToString() {
        String expectedToString = "Book{id=0, name='Sample Book', author='Sample Author', year=2023";
        assertEquals(expectedToString, book.toString());
    }

    @Test
    void testEquals() {
        Book sameBook = new Book("Sample Book", "Sample Author", 2023);
        Book differentBook = new Book("Different Book", "Different Author", 2024);

        assertEquals(book, sameBook);
        assertNotEquals(book, differentBook);
    }

    @Test
    void testHashCode() {
        Book sameBook = new Book("Sample Book", "Sample Author", 2023);
        Book differentBook = new Book("Different Book", "Different Author", 2024);

        assertEquals(book.hashCode(), sameBook.hashCode());
        assertNotEquals(book.hashCode(), differentBook.hashCode());
    }
}
