import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Task2 {

    /**
     * Calculates statistics of violations from .json files with data of violations during different years.
     * Creates file 'statistics.xml' with total amount of fines for every violation type sorted in descending order.
     *
     * @param path - path to a directory with .json files
     * @throws IOException
     */
    public void getStatistics(String path) throws IOException {
        Map<String, Double> statisticsMap = new HashMap<>();
        JsonFactory jasonFactory = new MappingJsonFactory();
        File dir = new File(path);
        File[] files = dir.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
        if (files == null) {
            throw new IllegalStateException("Given directory doesn't contain .json files");
        }
        for (File file : files) {
            try (JsonParser jsonParser = jasonFactory.createParser(file)) {
                if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("An array is expected");
                }
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    Violation violation = readViolation(jsonParser);
                    Double sum = statisticsMap.getOrDefault(violation.getType(), 0.0);
                    statisticsMap.put(violation.getType(), sum + violation.getAmount());
                }
            }
        }

        List<Violation> violationList = statisticsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new Violation(e.getKey(), e.getValue())).toList();

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.writeValue(new File(path + "/statistics.xml"), new Statistics(violationList));
    }

    private Violation readViolation(JsonParser jsonParser) throws IOException {
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("An object is expected");
        }

        Violation violation = new Violation();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();
            switch (property) {
                case "type" -> violation.setType(jsonParser.getText());
                case "fine_amount" -> violation.setAmount(jsonParser.getDoubleValue());
            }
        }
        return violation;
    }

}
