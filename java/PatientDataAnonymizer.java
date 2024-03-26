import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PatientDataAnonymizer {

    public static void main(String[] args) {
        // Input and output file paths
        String inputFile = "patient_data.txt";
        String outputFile = "anonymized_records.txt";
        String mappingFile = "mapping.txt";

        // Anonymize patient records
        anonymizePatientRecords(inputFile, outputFile, mappingFile);

        System.out.println("Anonymization complete.");
    }

    public static void anonymizePatientRecords(String inputFile, String outputFile, String mappingFile) {
        // Regex patterns
        Pattern namePattern = Pattern.compile("\\b(Mr\\.?|Ms\\.?|Mrs\\.?)\\s+([A-Za-z]+)\\s+([A-Za-z]+)\\b", Pattern.CASE_INSENSITIVE);
        Pattern dobPattern = Pattern.compile("\\b\\d{2}-\\d{2}-\\d{4}\\b");
        Pattern addressPattern = Pattern.compile("\\b\\d+\\s+[\\w\\s]+,[\\w\\s]+,\\s*[A-Z]{2}\\b");

        // Maps to store mappings between original data and anonymized IDs
        Map<String, String> nameMapping = new HashMap<>();
        Map<String, String> dobMapping = new HashMap<>();
        Map<String, String> addressMapping = new HashMap<>();

        // Counter for generating IDs
        int idCounter = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedWriter mappingWriter = new BufferedWriter(new FileWriter(mappingFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                // Anonymize name
                Matcher nameMatcher = namePattern.matcher(line);
                while (nameMatcher.find()) {
                    String fullName = nameMatcher.group(0);
                    String firstName = nameMatcher.group(2);
                    String lastName = nameMatcher.group(3);
                    String id = "ID" + idCounter++;

                    nameMapping.put(fullName, id);
                    nameMapping.put(firstName, id); // Map first name to same ID

                    line = line.replace(fullName, id);
                    line = line.replace(firstName, id); // Replace first name with ID
                }

                // Anonymize date of birth
                Matcher dobMatcher = dobPattern.matcher(line);
                while (dobMatcher.find()) {
                    String dob = dobMatcher.group();
                    String id = "ID" + idCounter++;

                    dobMapping.put(dob, id);

                    line = line.replace(dob, id);
                }

                // Anonymize address
                Matcher addressMatcher = addressPattern.matcher(line);
                while (addressMatcher.find()) {
                    String address = addressMatcher.group();
                    String id = "ID" + idCounter++;

                    addressMapping.put(address, id);

                    line = line.replace(address, id);
                }

                // Write anonymized line to output file
                bw.write(line);
                bw.newLine();
            }

            // Write mappings to mapping file
            writeMappings(mappingWriter, nameMapping, "Names");
            writeMappings(mappingWriter, dobMapping, "Date of Births");
            writeMappings(mappingWriter, addressMapping, "Addresses");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMappings(BufferedWriter writer, Map<String, String> mapping, String label) throws IOException {
        writer.write(label + ":\n");
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            writer.write(entry.getValue() + " " + entry.getKey() + "\n");
        }
        writer.newLine();
    }
}
