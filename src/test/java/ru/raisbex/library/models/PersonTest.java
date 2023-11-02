package ru.raisbex.library.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("John Doe", 30);
        person.setLogin("johndoe");
        person.setPassword("password123");
        person.setRole("USER");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("John Doe", person.getFIO());
        assertEquals(30, person.getAge());
        assertEquals("johndoe", person.getLogin());
        assertEquals("password123", person.getPassword());
        assertEquals("USER", person.getRole());

        person.setFIO("Jane Doe");
        assertEquals("Jane Doe", person.getFIO());

        person.setAge(35);
        assertEquals(35, person.getAge());

        person.setLogin("janedoe");
        assertEquals("janedoe", person.getLogin());

        person.setPassword("newpassword");
        assertEquals("newpassword", person.getPassword());

        person.setRole("ADMIN");
        assertEquals("ADMIN", person.getRole());
    }

    @Test
    void testToString() {
        Person person = new Person("John Doe", 30);
        person.setRole("USER");
        String expected = "Person{id=0, FIO='John Doe', age=30, role='USER'";

        // Validate the toString method of the Person class
        assertEquals(expected, person.toString(), "Person.toString() should produce the expected format");
    }

    @Test
    void testEquals() {
        Person samePerson = new Person("John Doe", 30);
        samePerson.setLogin("johndoe");
        samePerson.setPassword("password123");
        samePerson.setRole("USER");

        Person differentPerson = new Person("Jane Doe", 35);
        differentPerson.setLogin("janedoe");
        differentPerson.setPassword("newpassword");
        differentPerson.setRole("ADMIN");

        assertEquals(person, samePerson);
        assertNotEquals(person, differentPerson);
    }

    @Test
    void testHashCode() {
        Person samePerson = new Person("John Doe", 30);
        samePerson.setLogin("johndoe");
        samePerson.setPassword("password123");
        samePerson.setRole("USER");

        Person differentPerson = new Person("Jane Doe", 35);
        differentPerson.setLogin("janedoe");
        differentPerson.setPassword("newpassword");
        differentPerson.setRole("ADMIN");

        assertEquals(person.hashCode(), samePerson.hashCode());
        assertNotEquals(person.hashCode(), differentPerson.hashCode());
    }
}
