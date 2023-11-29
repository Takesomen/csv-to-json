package org.transformer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class PersonTest {
    @Test
    void parseDate() {
        Person person = Person.builder()
                .name("Александр")
                .surname("Петров")
                .sex("Мужчина")
                .email("askf@gmail.com")
                .phone("81654566123")
                .birthday("1999-09-08")
                .jobTitle("Пожарный")
                .build();
        Calendar correctBirthday = new GregorianCalendar(1999,8,8);
        Assertions.assertEquals(correctBirthday.getTime(), person.getBirthday());
    }
    @Test
    void countAge() {
        Person person = Person.builder()
                .name("Александр")
                .surname("Петров")
                .sex("Мужчина")
                .email("askf@gmail.com")
                .phone("81654566123")
                .birthday("1999-09-04")
                .jobTitle("Пожарный")
                .build();
        int correctAge = 24;
        Assertions.assertEquals(correctAge, person.getAge());
    }
}
