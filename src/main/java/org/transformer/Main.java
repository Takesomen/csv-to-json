package org.transformer;

import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.Gson;


public class Main {


    public static void main(String[] args) {
        System.out.println("Interactive mode? (y/n)");

        Scanner scan = new Scanner(System.in);
        String mode = scan.nextLine();

        if (Objects.equals(mode, "y")) {

            System.out.println("Input absolute path to CSV file");
            String filePath = scan.nextLine();
            File CSVFile = new File(filePath);

            if (!CSVFile.exists()) {
                System.out.println("File not found");
                System.exit(1);
            }

            try (var fr = new FileReader(filePath, StandardCharsets.UTF_8); var reader = new CSVReader(fr)) {

                String[] nextLine;
                List<Person> personList = new ArrayList<>();

                while ((nextLine = reader.readNext()) != null) {
                    Person person = new Person(Arrays.stream(nextLine, 2, 9).toArray(String[]::new));
                    personList.add(person);
                }

                System.out.println("Filter? Older than 20 years (y/n)");
                String option = scan.nextLine();
                if (Objects.equals(option, "y")) {
                    personList = personList.stream().filter(x -> x.age > 20).toList();
                }

                System.out.println("Sort? By name (y/n)");
                option = scan.nextLine();
                if (Objects.equals(option, "y")) {
                    personList = personList.stream().sorted(Comparator.comparing(Person::getName)).toList();
                }

                Map<String, List<Person>> groupedPersons = personList.stream().collect(Collectors.groupingBy(Person::getName));
                String newFileName = filePath.substring(0, filePath.length() - 3) + "json";

//                Writer writer = new FileWriter(newFileName, false)
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFileName), StandardCharsets.UTF_8)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
                    System.out.println("Group? By name (y/n)");
                    option = scan.nextLine();
                    if (Objects.equals(option, "y")) {
                        gson.toJson(groupedPersons, writer);
                    } else {
                        gson.toJson(personList, writer);
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            } catch (IOException | CsvValidationException | ParseException e) {
                System.out.println(e.getMessage());
            }
        } else if (Objects.equals(mode, "n")) {
            var app = new ConsoleApp();
            app.run(args);
        } else {
            System.out.println("Wrong input");
        }
    }
}