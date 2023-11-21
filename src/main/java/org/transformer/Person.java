package org.transformer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private Date parseDate(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.parse(date);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public long getAge() {
        return age;
    }

    public void outputName() {
        System.out.println(this.name + " " + this.surname + " " + this.age);
    }

    public String toString() {
        return String.format("Person {name: %s, surname: %s, sex: %s, email: %s, phone: %s, birthday: %s, jobTitle: %s, age: %d }",
                name, surname, sex, email, phone, birthday, jobTitle, age);
    }

    public Person(String[] informationList) throws ParseException {

        this.name = informationList[0];
        this.surname = informationList[1];
        this.sex = informationList[2];
        this.email = informationList[3];
        this.phone = informationList[4];
        this.birthday = parseDate(informationList[5]);
        this.jobTitle = informationList[6];
        this.age = countAge(this.birthday);
    }
}
