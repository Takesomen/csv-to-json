package org.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleApp {
    public void run(String[] args) {
        CommandLine line = parseArguments(args);
        if (line.hasOption("filename")) {

            System.out.println(line.getOptionValue("filename"));
            String fileName = line.getOptionValue("filename");
            List<Person> personList = readData(fileName);
            if (line.hasOption("filter")) {
                personList = personList.stream().filter(x -> x.age > 20).toList();
                writeJsonFile(fileName, personList);
            }
            if (line.hasOption("sort")) {
                personList = personList.stream().sorted(Comparator.comparing(Person::getName)).toList();
                writeJsonFile(fileName, personList);
            }
            if (line.hasOption("group")) {
                Map<String, List<Person>> groupedPersons = personList.stream().collect(Collectors.groupingBy(Person::getName));
                String newFileName = fileName.substring(0, fileName.length() - 3) + "json";
                try (Writer writer = new FileWriter(newFileName)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
                    gson.toJson(groupedPersons, writer);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
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

            System.err.println("Failed to parse command line arguments");
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
                Person person = new Person(Arrays.stream(nextLine, 2, 9).toArray(String[]::new));
                personList.add(person);
            }
        } catch (IOException | CsvValidationException | java.text.ParseException e) {
            System.out.println(e.getMessage());
        }
        return personList;
    }

    private void writeJsonFile(String fileName, List<Person> personList) {
        String newFileName = fileName.substring(0, fileName.length() - 3) + "json";
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
            gson.toJson(personList, writer);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Options getOptions() {
        var options = new Options();
        options.addOption("f", "filename", true, "file name to load data from");
        options.addOption("fil", "filter", false, "filter persons who younger than 20");
        options.addOption("s", "sort", false, "sort persons by their name");
        options.addOption("g", "group", false, "group persons by their name");
        return options;
    }

    private void printAppHelp() {

        Options options = getOptions();

        var formatter = new HelpFormatter();
        formatter.printHelp("JavaStatsEx", options, true);
    }
}
