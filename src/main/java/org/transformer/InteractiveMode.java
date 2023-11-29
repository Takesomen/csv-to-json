package org.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class InteractiveMode {

    static Logger log = Logger.getLogger(InteractiveMode.class.getName());

    public void run() {

        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("%h/interactiveLog.log", true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.addHandler(fileHandler);
        log.setUseParentHandlers(false);
        log.log(Level.INFO, "Запуск интерактивного режима");
        System.out.println("Введите полный путь до CSV файла");

        Scanner scan = new Scanner(System.in);
        String filePath = scan.nextLine();

        File csvFile = new File(filePath);
        List<Person> personList = readFile(csvFile);
        String absoluteCSVFilePath = csvFile.getAbsolutePath();

        System.out.println("Отфильтровать людей по возрасту? Старше 20 лет (True/False)");
        isInputCorrect(scan.hasNextBoolean());
        if (scan.nextBoolean()) {
            personList = personList.stream().filter(x -> x.age > 20).toList();
            log.log(Level.INFO, "Применен фильтр");
            writeJsonFile(absoluteCSVFilePath, personList);
        }

        System.out.println("Отсортировать по имени? (True/False)");
        isInputCorrect(scan.hasNextBoolean());
        if (scan.nextBoolean()) {
            personList = personList.stream().sorted(Comparator.comparing(Person::getName)).toList();
            log.info("Применена сортировка");
            writeJsonFile(absoluteCSVFilePath, personList);
        }

        System.out.println("Сгруппировать людей по имени? (True/False)");
        isInputCorrect(scan.hasNextBoolean());
        if (scan.nextBoolean()) {
            Map<String, List<Person>> groupedPersons = personList.stream().collect(Collectors.groupingBy(Person::getName));
            String newFileName = absoluteCSVFilePath.substring(0, absoluteCSVFilePath.length() - 3) + "json";
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
                log.log(Level.INFO, "Применена группировка");
                gson.toJson(groupedPersons, writer);
                log.log(Level.INFO, "Файл конвертирован в json");
            } catch (IOException ex) {
                log.log(Level.SEVERE, ex.getMessage());
            }
        }

    }

    private List<Person> readFile(File file) {

        if (!file.exists()) {
//            System.out.println("Файл не найден");
            log.log(Level.SEVERE, "Файл не найден");
            System.exit(1);
        }

        try (var fr = new FileReader(file.getAbsolutePath(), StandardCharsets.UTF_8);
             var reader = new CSVReader(fr)) {

            String[] nextLine;
            List<Person> personList = new ArrayList<>();

            while ((nextLine = reader.readNext()) != null) {
                Person person = Person.builder()
                        .name(nextLine[2])
                        .surname(nextLine[3])
                        .sex(nextLine[4])
                        .email(nextLine[5])
                        .phone(nextLine[6])
                        .birthday(nextLine[7])
                        .jobTitle(nextLine[8])
                        .build();
//                Person person = new Person(Arrays.stream(nextLine, 2, 9).toArray(String[]::new));
                personList.add(person);
            }
            return personList;

        } catch (IOException | CsvValidationException e) {
            log.log(Level.SEVERE, e.getMessage());
//            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private void writeJsonFile(String fileName, List<Person> personList) {
        String newFileName = fileName.substring(0, fileName.length() - 3) + "json";
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
            gson.toJson(personList, writer);
            log.log(Level.INFO, "Файл конвертирован в json");
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
//            System.out.println(ex.getMessage());
        }
    }

    private void isInputCorrect(boolean check) {
        if (!check) {
            log.log(Level.SEVERE, "Неправильный ввод");
//            System.out.println("Неправильный ввод");
            System.exit(1);
        }
    }


}
