package src;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;

public class EmailListCleaner {

    private static File file1;
    private static File file2;
    private static File outputFile = new File("C:/Hub/CleanedEmailList.csv");
    public static void main(String[] args) throws Exception {
        System.out.println("");
        System.out.println("********** Choose Exclusion list for first file, choose Mailing list second file. **********");
        System.out.println("");
        showFileChooser();

        try {
            removeEntriesFromFirstFile(file1, file2, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION && file1 == null) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected File1: " + selectedFile.getAbsolutePath());
            file1 = selectedFile;
            showFileChooser();
        } else if (result == JFileChooser.APPROVE_OPTION && file1 != null) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected File2: " + selectedFile.getAbsolutePath());
            file2 = selectedFile;
        } else {
            System.out.println("No file selected.");
        }
    }

    public static void removeEntriesFromFirstFile(File file1, File file2, File outputFile) throws IOException {
        Set<String> emailSet = getEmailSetFromFile(file2);

        try (CSVParser parser = new CSVParser(new FileReader(file1), CSVFormat.DEFAULT)) {
            try (Writer writer = new FileWriter(outputFile);
                 CSVParser emptyParser = CSVParser.parse("", CSVFormat.DEFAULT.withHeader())) {

                // Write the header to the output file
                emptyParser.getHeaderMap().forEach((key, value) -> {
                    try {
                        writer.append(key).append(",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.append(System.lineSeparator());

                // Iterate over records in file1
                for (CSVRecord record : parser) {
                    String email = record.get("email");

                    // Check if the email exists in file2
                    if (!emailSet.contains(email)) {
                        // Write the record to the output file
                        for (String field : record) {
                            writer.append(field).append(",");
                        }
                        writer.append(System.lineSeparator());
                    }
                }
            }
        }
    }

    private static Set<String> getEmailSetFromFile(File file) throws IOException {
        Set<String> emailSet = new HashSet<>();

        try (CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT)) {
            for (CSVRecord record : parser) {
                String email = record.get("email");
                emailSet.add(email);
            }
        }

        return emailSet;
    }

}
