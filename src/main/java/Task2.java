import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task2 {
    public void getStatistics(String path) throws IOException {
        JsonFactory jasonFactory = new MappingJsonFactory();
        File dir = new File(path);
        File[] files = dir.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
        for (File file : files) {
            JsonParser jsonParser = jasonFactory.createParser(file);
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        JsonNode node = jsonParser.readValueAsTree();
                        System.out.println("Type: " + node.get("type").asText());
                        System.out.println("Amount: " + node.get("fine_amount").asText());
                    }
                }
            }
            jsonParser.close();
        }
    }

    public static void main(String[] args) {
        Task2 task2 = new Task2();
        try {
            task2.getStatistics("files");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Year year = Year.of(2001);
        Random r = new Random();
        double rangeMin = 1.0;
        double rangeMax = 100.0;
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        while (year.isBefore(Year.now())) {
            String filePath = "fine" + year.toString() + ".json";
            List<Fine> list = new ArrayList<>();
            LocalDateTime date = LocalDateTime.of(year.getValue(), Month.JANUARY, 1, 0,0);
            while (date.isBefore(LocalDateTime.of(year.plusYears(1L).getValue(), Month.JANUARY, 1, 0,0))) {
                Fine fine = new Fine(date, "John", "Doe", ViolationType.randomViolation(), rangeMin + (rangeMax - rangeMin) * r.nextDouble());
                list.add(fine);
                date = date.plusDays(1);
            }
            try {
                mapper.writeValue(new File(filePath), list);
            } catch (IOException e) {
                e.printStackTrace();
            }
            year = year.plusYears(1);
        }*/
    }
}
