import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task1 {

    private final String OPEN_TAG = "<person";
    private final String CLOSE_TAG = "/>";
    private final Pattern OPEN_PATTERN = Pattern.compile("(.*?)" + OPEN_TAG + "\\b(.*)", Pattern.CASE_INSENSITIVE);
    private final Pattern CLOSE_PATTERN = Pattern.compile("(.*?)" + CLOSE_TAG + "(.*)");

    /**
     * Searches for 'person' elements in xml file,
     * merges values of 'name' and 'surname' attributes,
     * and deletes 'surname' attribute.
     * Creates new xml file with postfix '_processed' added to given file name.
     *
     * @param fileName path to given xml file
     * @throws IOException
     */
    public void mergeName(String fileName) throws IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("File name must be not null");
        }
        if (".xml".equalsIgnoreCase(fileName.substring(fileName.length() - 5))) {
            throw new IllegalArgumentException("File name must ends with '.xml'");
        }
        String outputFileName = fileName.substring(0, fileName.length() - 4) + "_processed.xml";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName));
             PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)))) {
            String line;
            StringBuilder element = new StringBuilder();
            boolean personIsOpen = false;
            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    out.println();
                }
                while (!"".equals(line)) {
                    if (personIsOpen) {
                        Matcher matcher = CLOSE_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            element.append(matcher.group(1)).append(CLOSE_TAG);
                            line = matcher.group(2);
                            out.print(handleElement(String.valueOf(element)));
                            element = new StringBuilder();
                            personIsOpen = false;
                            if ("".equals(line)) {
                                out.println();
                            }
                        } else {
                            element.append(line).append("\n");
                            line = "";
                        }
                    } else {
                        Matcher matcher = OPEN_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            out.print(matcher.group(1));
                            element.append(OPEN_TAG);
                            personIsOpen = true;
                            line = matcher.group(2);
                            if ("".equals(line)) {
                                element.append("\n");
                            }
                        } else {
                            out.println(line);
                            line = "";
                        }
                    }
                }
            }
        }
    }

    private String handleElement(String element) {
        final Pattern NAME_PATTERN = Pattern.compile(".*\\s+name\\s*=\\s*\"(.*?)\".*", Pattern.DOTALL);

        String surname = getSurnameValue(element);
        String result = deleteSurnameAttribute(element);

        if ("".equals(surname)) {
            return result;
        }

        Matcher matcher = NAME_PATTERN.matcher(result);
        if (matcher.matches()) {
            String name = matcher.group(1);
            result = matcher.group(0).replaceFirst(matcher.group(1), name + " " + surname);
        }
        return result;
    }

    private String getSurnameValue(String element) {
        final Pattern SURNAME_PATTERN = Pattern.compile(".*\\s+surname\\s*=\\s*\"(.*?)\".*", Pattern.DOTALL);
        Matcher matcher = SURNAME_PATTERN.matcher(element);
        String surname = "";
        if (matcher.matches()) {
            surname = matcher.group(1);
        }
        return surname;
    }

    private String deleteSurnameAttribute(String element) {
        final Pattern DELETE_PATTERN = Pattern.compile(".*(\\s+surname\\s*=\\s*\".*?\").*", Pattern.DOTALL);
        Matcher matcher = DELETE_PATTERN.matcher(element);
        String result = element;
        if (matcher.matches()) {
            result = matcher.group(0).replaceFirst(matcher.group(1), "");
        }
        return result;
    }

    public static void main(String[] args) {
        Task1 task1 = new Task1();
        try {
            long startTime = System.currentTimeMillis();
            task1.mergeName("task1.xml");
            long endTime = System.currentTimeMillis();
            System.out.println("That took " + (endTime - startTime) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}