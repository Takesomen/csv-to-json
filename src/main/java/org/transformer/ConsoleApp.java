package org.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class ConsoleApp {
    static Logger log = Logger.getLogger(InteractiveMode.class.getName());


    public void run(String[] args) {

        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("%h/consoleLog.log", true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.setUseParentHandlers(false);
        log.addHandler(fileHandler);

        log.log(Level.INFO, "Запуск консольного режима");

        CommandLine line = parseArguments(args);
        if (line.hasOption("filename")) {

            System.out.println(line.getOptionValue("filename"));
            String fileName = line.getOptionValue("filename");
            List<Person> personList = readData(fileName);
            if (line.hasOption("filter")) {
                personList = personList.stream().filter(x -> x.age > 20).toList();
                log.log(Level.INFO, "Применен фильтр");
                writeJsonFile(fileName, personList);
            }
            if (line.hasOption("sort")) {
                personList = personList.stream().sorted(Comparator.comparing(Person::getName)).toList();
                log.log(Level.INFO, "Применена сортировка");
                writeJsonFile(fileName, personList);
            }
            if (line.hasOption("group")) {
                Map<String, List<Person>> groupedPersons = personList.stream().collect(Collectors.groupingBy(Person::getName));
                String newFileName = fileName.substring(0, fileName.length() - 3) + "json";
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
                    log.log(Level.INFO, "Применена группировка");
                    gson.toJson(groupedPersons, writer);
                    log.log(Level.INFO, "Файл конвертирован в json");
                } catch (IOException ex) {
                    log.log(Level.SEVERE, ex.getMessage());
                }
            }
            if ((!line.hasOption("group")) && (!line.hasOption("sort")) && (!line.hasOption("filter"))) {
                writeJsonFile(fileName, personList);
            }
        } else {
            printAppHelp();
        }
    }

    private CommandLine parseArguments(String[] args) {

        Options options = getOptions();
        CommandLine line = null;

        CommandLineParser parser = new DefaultParser();

        try {
            line = parser.parse(options, args);

        } catch (ParseException ex) {
            log.log(Level.SEVERE, "Ошибка при считывании аргументов");
            System.err.println(ex.toString());
            printAppHelp();

            System.exit(1);
        }

        return line;
    }

    private List<Person> readData(String fileName) {
        List<Person> personList = null;
        try (var fr = new FileReader(fileName, StandardCharsets.UTF_8); var reader = new CSVReader(fr)) {
            String[] nextLine;
            personList = new ArrayList<>();
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
        } catch (IOException | CsvValidationException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return personList;
    }

    private void writeJsonFile(String fileName, List<Person> personList) {
        String newFileName = fileName.substring(0, fileName.length() - 3) + "json";
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
            gson.toJson(personList, writer);
            log.log(Level.INFO, "Файл конвертирован в json");
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
    }

    private Options getOptions() {
        var options = new Options();
        options.addOption("f", "filename", true, "название файла с которого будут загружены данные");
        options.addOption("fil", "filter", false, "отфильтровать людей старше 20 лет");
        options.addOption("s", "sort", false, "отсортировать людей по их именам");
        options.addOption("g", "group", false, "сгруппировать людей по их именам");
        return options;
    }

    private void printAppHelp() {

        Options options = getOptions();

        var formatter = new HelpFormatter();
        formatter.printHelp("JavaStatsEx", options, true);
    }
}
