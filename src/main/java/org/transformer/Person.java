package org.transformer;

import lombok.Builder;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Data
public class Person {

    String name;
    String surname;
    String sex;
    String email;
    String phone;
    Date birthday;
    String jobTitle;
    long age;

    private long countAge(Date birthday) {
        Date nowDate = new Date();
        long difference = nowDate.getTime() - birthday.getTime();
        return TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS) / 60 / 24 / 365;
    }

    private Date parseDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void outputName() {
        System.out.println(this.name + " " + this.surname + " " + this.age);
    }

    @Override
    public String toString() {
        return String.format("Person {name: %s, surname: %s, sex: %s, email: %s, phone: %s, birthday: %s, jobTitle: %s, age: %d }",
                name, surname, sex, email, phone, birthday, jobTitle, age);
    }

    @Builder
    public Person(String name, String surname, String sex, String email,
                  String phone, String birthday, String jobTitle) {

        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.email = email;
        this.phone = phone;
        this.birthday = parseDate(birthday);
        this.jobTitle = jobTitle;
        this.age = countAge(this.birthday);
    }

}
